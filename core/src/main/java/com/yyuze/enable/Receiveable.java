package com.yyuze.enable;

import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public interface Receiveable {

    <T extends BasePacket> void receive(T packet);
}
