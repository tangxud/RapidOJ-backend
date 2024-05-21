package com.yupi.yuojbackenduidgenerator.controller;

import com.yupi.yuojbackendserviceclient.service.UidFeignClient;
import com.yupi.yuojbackenduidgenerator.service.IWorkerNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tangx
 */
@RestController
@RequestMapping("/")
public class UidGeneratorController implements UidFeignClient {

    @Autowired
    private IWorkerNodeService workerNodeService;

    @Override
    @GetMapping("/generate")
    public long generateUid() {
        return workerNodeService.genUid();
    }
}
