package com.yupi.yuojbackendgateway.filter;

import cn.hutool.core.date.DateTime;
import com.yupi.yuojbackendcommon.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.yupi.yuojbackendcommon.constant.SecurityConstants.DETAILS_USER_ID;
import static com.yupi.yuojbackendcommon.constant.SecurityConstants.USER_KEY;
import static com.yupi.yuojbackendcommon.constant.TokenConstants.AUTHENTICATION;

@Component
@Slf4j
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        HttpHeaders headers = serverHttpRequest.getHeaders();
        String path = serverHttpRequest.getURI().getPath();

        // 跳过不需要验证的路径
        if (isWhiteListedPath(path)) {
            log.info("路径【{}】, 时间 【{}】，放行成功", path, DateTime.now());
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        // 判断路径中是否包含 inner，只允许内部调用
        if (antPathMatcher.match("/**/inner/**", path)) {
            return setResponse(response, HttpStatus.FORBIDDEN, "无权限", path);
        }

        // 统一权限校验 JWT校验
        String accessToken = JwtUtil.replaceTokenPrefix(headers.getFirst(AUTHENTICATION));
        if (StringUtils.isBlank(accessToken) || !JwtUtil.isAccessTokenValid(accessToken)) {
            return setResponse(response, HttpStatus.UNAUTHORIZED, "请重新登录", path);
        }

        // 解析Token获取用户信息
        Map<String, Object> claims = JwtUtil.parseToken(accessToken);
        long userId = (long) claims.get(DETAILS_USER_ID);
        String userKey = JwtUtil.getUserKey(accessToken);

        // 将用户信息添加到请求头中
        ServerHttpRequest modifiedRequest = serverHttpRequest.mutate()
                .header(USER_KEY, userKey)
                .header(DETAILS_USER_ID, String.valueOf(userId))
                .build();

        log.info("路径【{}】, 时间 【{}】，放行成功", path, DateTime.now());
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isWhiteListedPath(String path) {
        return antPathMatcher.match("/api/auth/login", path) ||
                antPathMatcher.match("/api/auth/logout", path) ||
                antPathMatcher.match("/api/auth/register", path) ||
                antPathMatcher.match("/api/auth/refresh", path) ||
                antPathMatcher.match("/**/v2/api-docs/**", path) ||
                antPathMatcher.match("/doc.html", path);
    }

    private Mono<Void> setResponse(ServerHttpResponse response, HttpStatus status, String message, String path) {
        response.setStatusCode(status);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer dataBuffer = dataBufferFactory.wrap(message.getBytes(StandardCharsets.UTF_8));
        log.info("路径【{}】, 时间 【{}】，拦截成功", path, DateTime.now());
        return response.writeWith(Mono.just(dataBuffer));
    }

    /**
     * 优先级提到最高
     * @return
     */
    @Override
    public int getOrder() {
        return -200;
    }
}
