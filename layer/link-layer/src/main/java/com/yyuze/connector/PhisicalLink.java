package com.yyuze.connector;

import com.yyuze.device.NetworkAdapter;
import com.yyuze.pkg.Frame;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class PhisicalLink {

    //bandwidth = 10Mbps = 10*10^6 bit/s = 10^7/8 byte/s = 1250000 byte/s
    public static long BANDWIDTH = 1250000L;

    //initialed 13 avaliable chanels
    //0 -> avaliable
    //1 -> busy
    private Byte[] avaliableChanel;

    private long[] avaliableTimeFrame;

    private ArrayList<NetworkAdapter> networkAdapters;

    private ArrayList<Byte> bytesInLink;

    private Frame frameInLink;

    private void init() {
        this.networkAdapters = new ArrayList<NetworkAdapter>();
        this.avaliableChanel = new Byte[13];

    }

    public PhisicalLink() {
        this.init();
    }

    public void join(NetworkAdapter networkAdapter) {
        this.networkAdapters.add(networkAdapter);
    }

    public boolean transmit(Frame frame) {
        this.frameInLink = frame;
        this.boardcastFrameInLink();
        this.drop();
        return false;
    }

    private void boardcastFrameInLink() {
        Frame frame = this.frameInLink;
        for (NetworkAdapter networkAdapter : networkAdapters) {
            if (networkAdapter.MAC != frame.getSourceMAC()) {
                networkAdapter.receiveFromLink(frame);
            }
        }
    }

    private void drop() {
        this.frameInLink = null;
    }


}
