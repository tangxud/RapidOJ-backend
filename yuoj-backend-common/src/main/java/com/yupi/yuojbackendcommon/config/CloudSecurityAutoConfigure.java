package com.yupi.yuojbackendcommon.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(CloudSecurityProperties.class)
public class CloudSecurityAutoConfigure{

    @Bean
    public CloudSecurityInterceptorConfigure cloudSecurityInterceptorConfigure() {
        return new CloudSecurityInterceptorConfigure();
    }

}