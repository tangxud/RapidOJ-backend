package com.yupi.yuojbackendauth.service;

import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.exception.BusinessException;
import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import com.yupi.yuojbackendmodel.model.entity.User;
import com.yupi.yuojbackendserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

/**
 * @Author tangx
 * @Date 2024/4/16 下午12:25
 */
@Service
@Slf4j
public class SysLoginService {

    private static final String SALT = "txdhwd";

    @Resource
    private UserFeignClient userFeignClient;

    public LoginUser login(String userAccount, String userPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        LoginUser loginUser = userFeignClient.getUserInfoByUserAccount(userAccount);
        // 用户不存在
        if (loginUser == null) {
            SysLoginService.log.info("user login failed, userAccount is not exists");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 校验用户账户密码
        if (!loginUser.getUser().getUserPassword().equals(encryptPassword)) {
            SysLoginService.log.info("user login failed, userAccount is not match");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户密码不匹配");
        }
        return loginUser;
    }

    public void register(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        User user = new User();
        user.setUserAccount(userAccount);
        // 加密密码
        user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes()));
        boolean success = userFeignClient.register(user);
        if (!success) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册失败，用户账号已注册");
        }
    }
}
