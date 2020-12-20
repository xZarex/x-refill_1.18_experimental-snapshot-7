package com.doo.xrefill.config;

/**
 * 选项
 */
public class Option {

    /**
     * 是否开启替换 --- 默认开启
     */
    public boolean enable = true;

    /**
     * 点击启用
     *
     * @return 修改后的值
     */
    public boolean clickEnable() {
        return enable = !enable;
    }
}
