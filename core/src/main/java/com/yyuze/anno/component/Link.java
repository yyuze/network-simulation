package com.yyuze.anno.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link {

    /**
     * 链路序列号
     * @return
     */
    long serial();

    /**
     * 链路上的设备MAC地址
     * @return
     */
    long[] MACs();

    /**
     * 链路上的交换机序列号
     * @return
     */
    long[] switchs();

}
