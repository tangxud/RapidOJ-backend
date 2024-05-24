package com.yupi.yuojbackendcommon.interceptor;

import com.alibaba.fastjson.JSON;
import com.yupi.yuojbackendcommon.common.BaseResponse;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.common.ResultUtils;
import com.yupi.yuojbackendcommon.config.CloudSecurityProperties;
import com.yupi.yuojbackendcommon.constant.CloudConstant;
import com.yupi.yuojbackendcommon.constant.SecurityConstants;
import com.yupi.yuojbackendcommon.utils.CustomHttpServletRequestWrapper;
import com.yupi.yuojbackendcommon.utils.JwtUtil;
import com.yupi.yuojbackendcommon.utils.StringUtils;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static com.yupi.yuojbackendcommon.constant.TokenConstants.AUTHENTICATION;

/**
 * @author tangx
 */
@Setter
@Slf4j
public class ServerProtectInterceptor implements HandlerInterceptor {

    private CloudSecurityProperties properties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        log.info("ServerProtectInterceptor - Request URI: " + request.getRequestURI());
        if (!properties.getOnlyFetchByGateway()) {
            // 统一权限校验 JWT 校验
            String accessToken = JwtUtil.replaceTokenPrefix(request.getHeader(AUTHENTICATION));
            if (StringUtils.isBlank(accessToken) || !JwtUtil.isAccessTokenValid(accessToken)) {
                return setResponse(response);
            }

            // 解析 Token 获取用户信息
            Map<String, Object> claims = JwtUtil.parseToken(accessToken);
            long userId = (long) claims.get(SecurityConstants.DETAILS_USER_ID);
            String userKey = JwtUtil.getUserKey(accessToken);

            // 使用 CustomHttpServletRequestWrapper 包装请求
            CustomHttpServletRequestWrapper wrappedRequest = new CustomHttpServletRequestWrapper(request);
            wrappedRequest.addHeader(SecurityConstants.USER_KEY, userKey);
            wrappedRequest.addHeader(SecurityConstants.DETAILS_USER_ID, String.valueOf(userId));

            // 将包装后的请求设置到 request 属性中，以便下一个拦截器使用
            request.setAttribute("wrappedRequest", wrappedRequest);
            return true;
        }

        String token = request.getHeader(CloudConstant.GATEWAY_TOKEN_HEADER);
        String gatewayToken = new String(Base64Utils.encode(CloudConstant.GATEWAY_TOKEN_VALUE.getBytes()));

        if (StringUtils.equals(gatewayToken, token)) {
            return true;
        } else {
            BaseResponse baseResponse = ResultUtils.error(ErrorCode.FORBIDDEN_ERROR, "请通过网关访问资源");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                // 使用 FastJSON 将 BaseResponse 实例转换为 JSON 字符串
                String jsonResponse = JSON.toJSONString(baseResponse);
                // 将 JSON 字符串写回客户端
                writer.write(jsonResponse);
                writer.flush();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private boolean setResponse(HttpServletResponse response) {
        BaseResponse baseResponse = ResultUtils.error(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try (PrintWriter writer = response.getWriter()) {
            String jsonResponse = JSON.toJSONString(baseResponse);
            writer.write(jsonResponse);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
