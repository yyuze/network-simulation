package com.yyuze.enums;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public enum Command {

    /**
     * 该命令为在链路层中发送一个帧
     * 格式：
     * source
     * target
     * payload
     *
     *
     * 例:
     * 0x00000001
     * 0x00000002
     * "hello world"
     *
     *
     */
    link_rcv,


}
