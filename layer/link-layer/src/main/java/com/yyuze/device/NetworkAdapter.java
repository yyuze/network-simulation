package com.yyuze.device;

import com.yyuze.connector.PhisicalLink;
import com.yyuze.pkg.Frame;
import com.yyuze.table.AddressResolutionProtocolTable;
import com.yyuze.tool.CRC;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */

/**
 * 使用协议：
 * 数据校验：CRC
 * 多路访问：CSMA/CD
 */
public class NetworkAdapter {

    private CRC crcTool = new CRC();

    public long MAC;

    private AddressResolutionProtocolTable arpTable;

    private PhisicalLink link;

    private ArrayList<Frame> buffer;


    public void joinLink(PhisicalLink link) {
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
        frame.setCRC(this.generateCRC(frame));
        this.buffer.add(frame);
    }

    public void send() {
        for (Frame frame : buffer) {
            this.link.transfer(frame);
            this.buffer.remove(frame);
        }
    }

    public void receiveFromLink(Frame frame) {
        if (frame.getTargetMAC() == this.MAC) {
            if (this.check(frame)) {
                this.buffer.add(frame);
            }
        }
    }

    private boolean check(Frame frame) {
        return this.crcTool.check(frame.getPayload(), frame.getCRC());
    }

    private long generateCRC(Frame frame) {
        return this.crcTool.generateCRC(frame.getPayload());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(NetworkAdapter.class)) {
            NetworkAdapter another = (NetworkAdapter) obj;
            return this.MAC == another.MAC;
        } else {
            return false;
        }
    }

}
