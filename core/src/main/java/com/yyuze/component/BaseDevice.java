package com.yyuze.component;

import com.yyuze.enable.UpwardTransmitable;
import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public abstract class BaseDevice <T extends BasePacket> {

    protected UpwardTransmitable<T> platform;

    protected abstract void sendToUpper();

    protected abstract void sendToLower();

    protected abstract <T extends BasePacket> void receiveFromUpper(T packet) throws PacketTypeException;

    protected abstract <T extends BasePacket> void receiveFromLower(T packet) throws PacketTypeException;

    public void setPlatform(UpwardTransmitable<T> platform) {
        this.platform = platform;
    }
}
