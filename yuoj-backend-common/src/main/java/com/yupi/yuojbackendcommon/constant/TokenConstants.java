package com.yupi.yuojbackendcommon.constant;

/**
 * Token的Key常量
 * 
 * @author ruoyi
 */
public interface TokenConstants
{
    /**
     * 令牌自定义标识
     */
    public static final String AUTHENTICATION = "Authorization";

    /**
     * 令牌前缀
     */
    public static final String PREFIX = "Bearer ";

    /**
     * 令牌秘钥
     */
    public final static String SECRET = "abcdefghijklmnopqrstuvwxyz";

    /**
     * access token的 map key
     */
    public final static String ACCESS_TOKEN = "access_token";
}