package com.yupi.yuojbackendauth.service;

import cn.hutool.core.lang.UUID;
import com.yupi.yuojbackendcommon.constant.SecurityConstants;
import com.yupi.yuojbackendcommon.utils.JwtUtil;
import com.yupi.yuojbackendcommon.utils.StringUtils;
import com.yupi.yuojbackendmodel.model.entity.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.yupi.yuojbackendcommon.constant.CacheConstants.*;
import static com.yupi.yuojbackendcommon.constant.TokenConstants.AUTHENTICATION;

/**
 * @Author tangx
 * @Date 2024/4/16 上午11:36
 */

@Service
@Slf4j
public class TokenService {

    private static final long MILLIS_SECOND = 1000;
    private static final long MILLIS_MINUTE = MILLIS_SECOND * 60;
    private static final long REFRESH_TIME_TO_MILLIS = REFRESH_TIME * MILLIS_MINUTE;

    @Autowired
    private RedisService redisService;

    /**
     * 创建令牌
     */
    public Map<String, Object> createToken(LoginUser loginUser)
    {
        String token = UUID.fastUUID().toString();
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        // 密码脱敏
        loginUser.getUser().setUserPassword("");
        refreshToken(loginUser);

        // Jwt存储信息
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        claimsMap.put(SecurityConstants.USER_KEY, token);
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, loginUser.getUser().getId());

        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>();
        rspMap.put("access_token", JwtUtil.createAccessToken(claimsMap));
        rspMap.put("expires_in", ACCESS_TOKEN_EXPIRATION);
        return rspMap;
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param LoginUser
     */
    public void verifyToken(LoginUser LoginUser)
    {
        long expireTime = LoginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= REFRESH_TIME_TO_MILLIS)
        {
            refreshToken(LoginUser);
        }
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取token
        String token = JwtUtil.replaceTokenPrefix(request.getHeader(AUTHENTICATION));
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token)
    {
        LoginUser loginUser = null;
        try
        {
            if (StringUtils.isNotEmpty(token))
            {
                String userkey = JwtUtil.getUserKey(token);
                loginUser = redisService.getCacheObject(getTokenKey(userkey));
                return loginUser;
            }
        }
        catch (Exception e)
        {
            log.error("获取用户信息异常'{}'", e.getMessage());
        }
        return loginUser;
    }

    private String getTokenKey(String userkey) {
        return LOGIN_TOKEN_KEY + userkey;
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + ACCESS_TOKEN_EXPIRATION * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisService.setCacheObject(userKey, loginUser, ACCESS_TOKEN_EXPIRATION, TimeUnit.MINUTES);
    }

    /**
     * 删除用户缓存信息
     * @param token
     * @return 删除成功返回true，失败返回false
     */
    public boolean delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = JwtUtil.getUserKey(token);
            redisService.deleteObject(getTokenKey(userKey));
            return true;
        }
        return false;
    }
}
