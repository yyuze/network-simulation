package com.yyuze.device;

import com.yyuze.BasePlatform;
import com.yyuze.packet.BasePacket;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public abstract class BaseDevice{

    protected BasePlatform runtime;

    public <T extends BasePacket> void sendToRuntime(T packet){
        this.runtime.receive(packet);
    }

    public abstract <T extends BasePacket> void receive(T packet) throws Exception ;

}
