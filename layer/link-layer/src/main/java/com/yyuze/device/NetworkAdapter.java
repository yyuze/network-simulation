package com.yyuze.device;

import com.yyuze.connector.PhisicalLink;
import com.yyuze.pkg.Frame;
import com.yyuze.table.AddressResolutionProtocolTable;
import com.yyuze.tool.CRC;

import java.util.ArrayList;
import java.util.Random;

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

    private int collisionCounter;

    private MessageContorller messageContorller;

    private class MessageContorller {

        private long BIT_TIME_512;

        private Random randomTool;

        private long transferAllow;

        public MessageContorller() {
            this.BIT_TIME_512 = 512000000 / PhisicalLink.BANDWIDTH;
            this.transferAllow = this.getMicrotimeStamp();
            this.randomTool = new Random();
        }

        private long getMicrotimeStamp() {
            return System.nanoTime() / 1000;
        }

        private long generateBinaryExponentialBackoff() {
            return this.randomTool.nextInt((int) Math.pow(2, collisionCounter)) * this.BIT_TIME_512;
        }

        private void pause() {
            this.transferAllow += this.generateBinaryExponentialBackoff();
        }

        private void reset() {
            this.transferAllow = this.getMicrotimeStamp();
        }

        private boolean isAllowedTransfer() {
            return this.transferAllow <= this.getMicrotimeStamp();
        }

    }

    public void joinLink(PhisicalLink link) {
        this.link = link;
        this.link.join(this);
    }

    /**
     *将缓存区中的帧发送至链路
     */
    public void sendToLink() {
        if (this.messageContorller.isAllowedTransfer()) {
            for (Frame frame : buffer) {
                if (!this.link.transmit(frame)) {
                    this.collisionCounter++;
                    this.messageContorller.pause();
                    break;
                } else {
                    this.messageContorller.reset();
                    this.buffer.remove(frame);
                    this.collisionCounter = 0;
                }

            }
        }
    }

    /**
     * 提供给网络层传输数据的api
     * @param networkLayerData 网络层传下来的数据
     */
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

    /**
     * 提供给链路传入数据的api
     * @param frame 从链路获取的帧
     */
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
