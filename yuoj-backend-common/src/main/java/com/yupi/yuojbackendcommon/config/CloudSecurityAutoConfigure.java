package com.yupi.yuojbackendcommon.config;

import com.yupi.yuojbackendcommon.service.RedisService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author tangx
 */
@Configuration
@EnableConfigurationProperties(CloudSecurityProperties.class)
public class CloudSecurityAutoConfigure {

    @Bean
    public CloudSecurityInterceptorConfigure cloudSecurityInterceptorConfigure(CloudSecurityProperties properties, RedisService redisService) {
        return new CloudSecurityInterceptorConfigure(properties, redisService);
    }
}
