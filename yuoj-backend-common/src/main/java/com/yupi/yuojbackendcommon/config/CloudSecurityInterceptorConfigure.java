package com.yupi.yuojbackendcommon.config;

import com.yupi.yuojbackendcommon.interceptor.ServerProtectInterceptor;
import com.yupi.yuojbackendcommon.interceptor.UserTokenInterceptor;
import com.yupi.yuojbackendcommon.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author tangx
 */
public class CloudSecurityInterceptorConfigure implements WebMvcConfigurer {

    private final CloudSecurityProperties properties;
    private final RedisService redisService;

    /** 不需要拦截地址 */
    public static final String[] EXCLUDE_URLS = { "/login", "/logout", "/refresh", "/register", "/**/inner/**"};

    @Autowired
    public CloudSecurityInterceptorConfigure(CloudSecurityProperties properties, RedisService redisService) {
        this.properties = properties;
        this.redisService = redisService;
    }

    @Bean
    public HandlerInterceptor serverProtectInterceptor() {
        ServerProtectInterceptor interceptor = new ServerProtectInterceptor();
        interceptor.setProperties(properties);
        return interceptor;
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor(redisService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // serverProtectInterceptor 拦截每一个路径
        registry.addInterceptor(serverProtectInterceptor()).order(100).addPathPatterns("/**");

        // userTokenInterceptor 排除指定的路径
        registry.addInterceptor(userTokenInterceptor()).order(200)
                .excludePathPatterns(EXCLUDE_URLS);
    }
}
