package com.yyuze.enable;

import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-04
 */
public interface DownwardTransmitable <T extends BasePacket> {

    /**
     * 由于网络层次的抽象，导致设备的分布式被中心化了，id用于匹配不同层上的同一个设备
     * @param id
     * @param packet
     */
    void tansmitToLower(long id,T packet);

}
