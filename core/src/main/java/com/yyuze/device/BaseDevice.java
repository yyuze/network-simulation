package com.yyuze.device;

import com.yyuze.BasePlatform;
import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public abstract class BaseDevice <Platform extends BasePlatform>{

    protected Platform runtime;

    protected  <T extends BasePacket> void sendToRuntime(T packet){
        this.runtime.receive(packet);
    }

    protected abstract void sendToUpper();

    protected abstract void sendToLower();

    protected abstract <T extends BasePacket> void receiveFromUpper(T packet) throws Exception;

    protected abstract <T extends BasePacket> void receiveFromLower(T packet) throws Exception;

}
