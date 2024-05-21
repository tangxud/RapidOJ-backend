package com.yupi.yuojbackenduidgenerator.service.impl;


import com.baidu.fsg.uid.UidGenerator;
import com.yupi.yuojbackenduidgenerator.service.IWorkerNodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WorkerNodeServiceImpl implements IWorkerNodeService {
    @Resource
    private UidGenerator uidGenerator;

    @Override
    public long genUid() {
        return uidGenerator.getUID();
    }
}
