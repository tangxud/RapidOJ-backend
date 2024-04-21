package com.yupi.yuojbackendauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.yupi")
@EnableFeignClients(basePackages = {"com.yupi.yuojbackendserviceclient.service"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class YuojBackendAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuojBackendAuthApplication.class, args);
    }

}
