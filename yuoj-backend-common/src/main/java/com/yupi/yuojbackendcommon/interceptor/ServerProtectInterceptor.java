package com.yupi.yuojbackendcommon.interceptor;

import com.alibaba.fastjson.JSON;
import com.yupi.yuojbackendcommon.common.BaseResponse;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.common.ResultUtils;
import com.yupi.yuojbackendcommon.config.CloudSecurityProperties;
import com.yupi.yuojbackendcommon.constant.CloudConstant;
import com.yupi.yuojbackendcommon.utils.StringUtils;
import lombok.NonNull;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerProtectInterceptor implements HandlerInterceptor {

    private CloudSecurityProperties properties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){

        if (!properties.getOnlyFetchByGateway()) {
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

    public void setProperties(CloudSecurityProperties properties) {
        this.properties = properties;
    }
}