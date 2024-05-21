package com.yupi.yuojbackenduidgenerator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.yupi.yuojbackenduidgenerator.mapper")
@ComponentScan("com.yupi")
@EnableFeignClients(basePackages = {"com.yupi.yuojbackendserviceclient.service"})
public class YuojBackendUidGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(YuojBackendUidGeneratorApplication.class, args);
    }

}
