package com.yupi.yuojbackendserviceclient.factory;

import com.yupi.yuojbackendserviceclient.service.JudgeFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteJudgeFallbackFactory implements FallbackFactory<JudgeFeignClient>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteJudgeFallbackFactory.class);

    @Override
    public JudgeFeignClient create(Throwable throwable)
    {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return questionSubmitId -> null;

    }
}
