package com.yupi.yuojbackendgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Order(0)
public class AddBase64EncodedRequestHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AddBase64EncodedRequestHeaderGatewayFilterFactory.Config> {

    public AddBase64EncodedRequestHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取原始的请求头值
            String originalValue = config.getValue();

            // 对原始值进行 Base64 编码
            String encodedValue = Base64.getEncoder().encodeToString(originalValue.getBytes(StandardCharsets.UTF_8));

            // 添加经过 Base64 编码的值到请求头中
            exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add(config.getHeaderName(), encodedValue));

            // 继续向下传递请求
            return chain.filter(exchange);
        };
    }

    public static class Config {
        private String headerName; // 请求头名称
        private String value; // 原始值

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
