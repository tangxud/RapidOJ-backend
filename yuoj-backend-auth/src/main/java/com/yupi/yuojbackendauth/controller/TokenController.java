package com.yupi.yuojbackendauth.controller;

import com.yupi.yuojbackendauth.service.TokenService;
import com.yupi.yuojbackendcommon.common.BaseResponse;
import com.yupi.yuojbackendcommon.common.ResultUtils;
import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author tangx
 * @Date 2024/4/16 上午11:36
 */
@RestController
public class TokenController {
    @Autowired
    private TokenService tokenService;


    @PostMapping("/refresh")
    public BaseResponse<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (!(loginUser == null))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return ResultUtils.success();
        }
        return ResultUtils.success();
    }


}
