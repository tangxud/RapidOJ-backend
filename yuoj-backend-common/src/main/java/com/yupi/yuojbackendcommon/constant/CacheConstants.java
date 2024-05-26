package com.yupi.yuojbackendcommon.constant;

/**
 * 缓存常量信息
 * 
 * @author ruoyi
 */
public interface CacheConstants
{
    /**
     * 消息id缓存
     */
    String MESSAGE_ID_PREFIX = "code:submission:";

    /**
     * 题目提交缓存
     */
    String CODE_SUBMISSION_KEY_PREFIX = "code:submission:";

    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long ACCESS_TOKEN_EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 密码最大错误次数
     */
    public final static int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public final static long PASSWORD_LOCK_TIME = 10;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
}
