package com.yupi.yuojbackendmodel.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 已登录用户视图（脱敏）
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 **/
@Data
public class LoginUser implements Serializable {

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户信息
     */
    private User user;

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 登录时间
     */
    private long loginTime;

    private static final long serialVersionUID = 1L;
}