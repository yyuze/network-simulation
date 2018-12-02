package com.yyuze.enable;

import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public interface Sendable {

    <T extends BasePacket> void send(T packet);
}
