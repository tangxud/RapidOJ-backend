package com.yupi.yuojbackendserviceclient.service;

import com.yupi.yuojbackendserviceclient.factory.RemoteUidFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author tangx
 * @Date 2024/5/21 19:55
 */
@FeignClient(name = "yuoj-backend-uid-generator", path = "/api/uid/inner", fallbackFactory = RemoteUidFallbackFactory.class)
public interface UidFeignClient {
    @GetMapping("/generate")
    long generateUid();
}
