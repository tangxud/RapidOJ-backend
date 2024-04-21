package com.yupi.yuojbackendcommon.constant;

/**
 * 通用常量
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface CommonConstant {

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    // todo 如果更换包名，记得改
    String[] JSON_WHITELIST_STR = { "org.springframework", "com.yupi" };
    
}
