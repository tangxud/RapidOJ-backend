package com.yupi.yuojbackenduserservice.controller.inner;

import com.yupi.yuojbackendcommon.utils.StringUtils;
import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import com.yupi.yuojbackendmodel.model.entity.User;
import com.yupi.yuojbackendserviceclient.service.UserFeignClient;
import com.yupi.yuojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据 id 获取用户
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据 id 获取用户列表
     * @param idList
     * @return
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }

    /**
     * 根据账户查询用户信息
     * @param userAccount
     * @return
     */
    @Override
    @GetMapping("/get/userInfo")
    public LoginUser getUserInfoByUserAccount(String userAccount) {
        if (StringUtils.isAnyBlank(userAccount)) {
            return null;
        }
        // todo 测试openFegin降级效果
//        boolean test = true;
//        if (test) {
//            int i = 10/0;
//        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(userService.getUserByUserAccount(userAccount));
        return loginUser;
    }

    @Override
    @PostMapping("/register")
    public boolean register(@RequestBody User user) {
        if (user == null) {
            return false;
        }
        if (!userService.checkUserAccountUnique(user)) {
            return false;
        }
        return userService.userRegister(user);
    }

}
