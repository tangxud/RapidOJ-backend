//package com.yupi.yuojbackendauth.interceptor;
//
//import cn.hutool.core.convert.Convert;
//import cn.hutool.core.date.DateTime;
//import com.yupi.yuojbackendcommon.service.RedisService;
//import com.yupi.yuojbackendauth.service.TokenService;
//import com.yupi.yuojbackendcommon.utils.JwtUtil;
//import com.yupi.yuojbackendcommon.utils.StringUtils;
//import com.yupi.yuojbackendmodel.model.entity.LoginUser;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
///**
// * @Author tangx
// * @Date 2024/4/17 上午10:33
// */
//@Component
//@Slf4j
//public class RefreshInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private TokenService  tokenService;
//
//    @Autowired
//    private RedisService redisService;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String accessToken = JwtUtil.getToken(request);
//        if (StringUtils.isBlank(accessToken)) {
//            return false;
//        }
//        String userKey = JwtUtil.getUserKey(accessToken);
//        LoginUser loginUser = Convert.convert(LoginUser.class, redisService.getCacheObject(userKey));
//        if (loginUser == null) {
//            return false;
//        }
//        tokenService.refreshToken(loginUser);
//        log.info("用户【{}】, 时间 【{}】，刷新token成功", loginUser.getUser().getId(), DateTime.now());
//        return true;
//    }
//}
