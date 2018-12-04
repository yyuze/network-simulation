package com.yyuze.device;

import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.BasePacket;
import com.yyuze.packet.IPv4Packet;

/**
 * Author: yyuze
 * Time: 2018-12-04
 */
public class Router extends BaseDevice<IPv4Packet>{
    @Override
    public void sendToUpper() {

    }

    @Override
    public void sendToLower() {

    }

    @Override
    public void receiveFromLower(BasePacket packet) throws PacketTypeException {

    }

    @Override
    public void receiveFromUpper(BasePacket packet) throws PacketTypeException {

    }
}
