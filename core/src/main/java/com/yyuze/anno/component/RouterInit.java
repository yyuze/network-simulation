package com.yyuze.anno.component;

/**
 * Author: yyuze
 * Time: 2018-12-16
 */
public @interface RouterInit {

    /**
     * 路由器序列号
     * @return
     */
    long serial();

    /**
     * 路由器的适配器MAC
     * @return
     */
    long[] MACs();

}
