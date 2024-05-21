package com.yupi.yuojbackendserviceclient.factory;

import com.yupi.yuojbackendserviceclient.service.UidFeignClient;
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
public class RemoteUidFallbackFactory implements FallbackFactory<UidFeignClient>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUidFallbackFactory.class);

    @Override
    public UidFeignClient create(Throwable throwable)
    {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return new UidFeignClient()
        {
            @Override
            public long generateUid() {
                return -1;
            }
        };
    }
}
