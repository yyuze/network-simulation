package com.yyuze.device;

import com.yyuze.connector.PhisicalLink;
import com.yyuze.pkg.Frame;
import com.yyuze.table.AddressResolutionProtocolTable;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class NetworkAdapter {

    public Long MAC;

    private AddressResolutionProtocolTable arpTable;

    private PhisicalLink link;

    private ArrayList<Frame> buffer;

    //todo get a IP package
    public void receiveFromNetworkLayer(String networkLayerData){

        Frame frame = new Frame();
        //todo ???
        frame.setPreamble(0l);
        frame.setSourceMAC(this.MAC);
        frame.setTargetMAC(this.arpTable.getMACByIP(0l));
        //todo IP pakage constructure
        frame.setPayload("");
        frame.setType('a');
        //todo hashCRC
        frame.setCRC(0l);
        this.buffer.add(frame);

    }

    public void send() {
        for (Frame frame : buffer) {
            this.link.transfer(frame);
            this.buffer.remove(frame);
        }
    }

    public void receive(Frame frame) {
        if (frame.getTargetMAC().equals(this.MAC)) {
            this.buffer.add(frame);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(NetworkAdapter.class)) {
            NetworkAdapter another = (NetworkAdapter) obj;
            return this.MAC.equals(another.MAC);
        } else {
            return false;
        }
    }
}
