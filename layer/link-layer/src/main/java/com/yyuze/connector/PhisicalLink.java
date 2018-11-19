package com.yyuze.connector;

import com.yyuze.device.LinkInterface;
import com.yyuze.pkg.Frame;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class PhisicalLink {

    private Integer[] avaliableChanel;

    private Long[] avaliableTimeFrame;

    private LinkInterface[] linkInterfaces;

    public void transfer(LinkInterface sender, Frame frame) {
        for(LinkInterface linkInterface : linkInterfaces){
            if(!linkInterface.MAC.equals(sender.MAC)){
                linkInterface.receive(frame);
            }
        }
    }
}
