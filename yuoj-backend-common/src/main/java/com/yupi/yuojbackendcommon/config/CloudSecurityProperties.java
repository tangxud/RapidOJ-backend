package com.yupi.yuojbackendcommon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oj.cloud")
public class CloudSecurityProperties {

    /**
     * 是否只能通过网关获取资源
     * 默认为True
     */
    private Boolean onlyFetchByGateway = Boolean.TRUE;

}