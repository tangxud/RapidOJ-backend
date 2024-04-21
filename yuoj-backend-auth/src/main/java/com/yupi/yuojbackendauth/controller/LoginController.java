package com.yupi.yuojbackendauth.controller;

import com.yupi.yuojbackendauth.service.SysLoginService;
import com.yupi.yuojbackendauth.service.TokenService;
import com.yupi.yuojbackendcommon.common.BaseResponse;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.common.ResultUtils;
import com.yupi.yuojbackendcommon.constant.TokenConstants;
import com.yupi.yuojbackendcommon.exception.BusinessException;
import com.yupi.yuojbackendcommon.utils.JwtUtil;
import com.yupi.yuojbackendcommon.utils.StringUtils;
import com.yupi.yuojbackendmodel.model.dto.user.UserLoginRequest;
import com.yupi.yuojbackendmodel.model.dto.user.UserRegisterRequest;
import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author tangx
 * @Date 2024/4/16 下午1:52
 */
@RestController
public class LoginController {

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody UserLoginRequest userLoginRequest)
    {   if (userLoginRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 用户登录
        LoginUser userInfo = sysLoginService.login(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword());
        // 获取登录token
        return ResultUtils.success(tokenService.createToken(userInfo));
    }


    @DeleteMapping("/logout")
    public BaseResponse<?> logout(HttpServletRequest request)
    {
        // 从header获取token标识
        String token = JwtUtil.replaceTokenPrefix(request.getHeader(TokenConstants.AUTHENTICATION));
        boolean result = false;
        if (StringUtils.isNotEmpty(token))
        {
            // 删除用户token对应的key
            result = tokenService.delLoginUser(token);
        }
        if (result) {
            return ResultUtils.success();
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "登出失败");
    }

    @PostMapping("/register")
    public BaseResponse<?> register(@RequestBody UserRegisterRequest userRegisterRequest)
    {
        // 用户注册
        sysLoginService.register(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());
        return ResultUtils.success();
    }
}
