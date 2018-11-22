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

    private static Long GENERATOR_CRC32 = 0b100000100110000010001110110110111L;

    public Long MAC;

    private AddressResolutionProtocolTable arpTable;

    private PhisicalLink link;

    private ArrayList<Frame> buffer;

    public void joinLink(PhisicalLink link){
        this.link = link;
        this.link.join(this);
    }

    //todo get a IP package
    public void receiveFromNetworkLayer(String networkLayerData) {
        Frame frame = new Frame();
        frame.setSourceMAC(this.MAC);
        frame.setTargetMAC(this.arpTable.getMACByIP(0L));
        //todo IP pakage constructure
        frame.setPayload("");
        frame.setType('a');
        frame.setCRC(this.generateCRC(frame.getPayload()));
        this.buffer.add(frame);
    }

    public void send() {
        for (Frame frame : buffer) {
            this.link.transfer(frame);
            this.buffer.remove(frame);
        }
    }

    public void receiveFromLink(Frame frame) {
        if (frame.getTargetMAC().equals(this.MAC)) {
            if (this.isCorrect(frame)) {
                this.buffer.add(frame);
            }
        }
    }

    private boolean isCorrect(Frame frame) {
        Long crc = frame.getCRC();
        if (crc % NetworkAdapter.GENERATOR_CRC32 == 0) {
            return true;
        } else {
            return false;
        }

    }

    private Long generateCRC(String payload) {
        //todo CRC algorithm
        return 0L;
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
