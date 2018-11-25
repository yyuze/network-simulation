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

    private CRCTool crcTool = new CRCTool();

    private class CRCTool {

        private Long GENERATOR_CRC32 = 0b100000100110000010001110110110111L;

        public Long getCRC(String data) {
            byte[] bytes = data.getBytes();
            Long result = 0L;
            for (byte b : bytes) {
                result = this.xor(result, ((long) b) << 32);
            }
            return result % this.GENERATOR_CRC32;
        }

        public boolean check(String data,Long crc) {
            byte[] bytes = data.getBytes();
            Long result = 0L;
            for (byte b : bytes) {
                result = this.xor(result, (long) b);
            }
            return xor(result, crc) % this.GENERATOR_CRC32 == 0;
        }

        private Long xor(Long num1, Long num2) {
            return (~(num1 & num2)) & (num1 | num2);
        }
    }

    public Long MAC;

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
        if (frame.getTargetMAC().equals(this.MAC)) {
            if (this.check(frame)) {
                this.buffer.add(frame);
            }
        }
    }

    private boolean check(Frame frame) {
        return this.crcTool.check(frame.getPayload(),frame.getCRC());
    }

    private Long generateCRC(Frame frame) {
        return this.crcTool.getCRC(frame.getPayload());
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
