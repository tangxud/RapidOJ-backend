package com.yupi.yuojbackendserviceclient.factory;

import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import com.yupi.yuojbackendmodel.model.entity.User;
import com.yupi.yuojbackendserviceclient.service.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * 文件服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<UserFeignClient>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable throwable)
    {
        log.error("feign远程调用系统用户服务异常后的降级方法");
        return new UserFeignClient()
        {
            @Override
            public User getById(@RequestParam("userId") long userId) {
                return null;
            };

            @Override
            public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
                return null;
            };

            @Override
            public LoginUser getUserInfoByUserAccount(@RequestParam("userAccount") String userAccount) {
                return null;
            };

            @Override
            public boolean register(@RequestBody User user) {
                return false;
            };
        };
    }
}
