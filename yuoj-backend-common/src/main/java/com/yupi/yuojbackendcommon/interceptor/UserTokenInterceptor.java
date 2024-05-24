package com.yupi.yuojbackendcommon.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.yupi.yuojbackendcommon.constant.SecurityConstants;
import com.yupi.yuojbackendcommon.context.UserContextHolder;
import com.yupi.yuojbackendcommon.service.RedisService;
import com.yupi.yuojbackendcommon.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

import static com.yupi.yuojbackendcommon.constant.CacheConstants.LOGIN_TOKEN_KEY;

/**
 * 自定义请求头拦截器，将Header数据封装到线程变量中方便获取
 *
 * @author tangx
 */
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {

    private final RedisService redisService;

    public UserTokenInterceptor(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 获取包装后的请求
        HttpServletRequest wrappedRequest = (HttpServletRequest) request.getAttribute("wrappedRequest");
        if (wrappedRequest != null) {
            request = wrappedRequest;
        }

        log.info("UserTokenInterceptor - Request URI: " + request.getRequestURI());
        String userKey = request.getHeader(SecurityConstants.USER_KEY);
        String userId = request.getHeader(SecurityConstants.DETAILS_USER_ID);

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");

        if (StringUtils.isAnyBlank(userKey, userId)) {
            response.getWriter().write("未登录：请重新登录");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        Object loginUser = redisService.getCacheObject(getTokenKey(userKey));

        if (ObjectUtil.isEmpty(loginUser)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("未授权：用户会话已过期或无效.");
            return false;
        }

        UserContextHolder.set(SecurityConstants.LOGIN_USER, loginUser);
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        UserContextHolder.clear();
    }

    private String getTokenKey(String userKey) {
        return LOGIN_TOKEN_KEY + userKey;
    }
}
