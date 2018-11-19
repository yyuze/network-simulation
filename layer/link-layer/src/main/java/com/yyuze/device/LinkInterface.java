package com.yyuze.device;

import com.yyuze.connector.PhisicalLink;
import com.yyuze.pkg.Frame;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class LinkInterface {

    public Long MAC;

    private PhisicalLink link;

    private ArrayList<Frame> buffer;

    public void send() {
        for (Frame frame : buffer) {
            this.link.transfer(this,frame);
            this.buffer.remove(frame);
        }
    }


    public void receive(Frame frame) {
        this.buffer.add(frame);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(LinkInterface.class)) {
            LinkInterface another = (LinkInterface) obj;
            return this.MAC.equals(another.MAC);
        } else {
            return false;
        }
    }
}
