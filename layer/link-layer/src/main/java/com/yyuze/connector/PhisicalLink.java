package com.yyuze.connector;

import com.yyuze.device.NetworkAdapter;
import com.yyuze.pkg.Frame;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class PhisicalLink {

    private Integer[] avaliableChanel;

    private Long[] avaliableTimeFrame;

    private NetworkAdapter[] networkAdapters;

    public void transfer(Frame frame) {
        for(NetworkAdapter networkAdapter : networkAdapters){
            if(!networkAdapter.MAC.equals(frame.getSourceMAC())){
                networkAdapter.receive(frame);
            }
        }
    }
}
