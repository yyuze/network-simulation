package com.yyuze.component;

import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.BasePacket;
import com.yyuze.packet.Datagram;

/**
 * Author: yyuze
 * Time: 2018-12-04
 */
public class Router extends BaseDevice<Datagram>{
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
