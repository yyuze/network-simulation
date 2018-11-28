package com.yyuze.device;

import com.yyuze.pkg.IPv4Packet;
import com.yyuze.pkg.EthernetFrame;
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

    private CRC crcTool;

    public long MAC;

    private PhisicalLink link;

    private ArrayList<EthernetFrame> buffer;

    private int collisionCounter;

    private MessageContorller messageContorller;

    private AddressResolutionProtocolTable arpTable;

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

    public NetworkAdapter(long MAC,PhisicalLink link){
        this.crcTool = new CRC();
        this.MAC = MAC;
        this.joinLink(link);
        this.buffer = new ArrayList<>();
        this.collisionCounter = 0;
        this.messageContorller = new MessageContorller();
        //todo arp
    }

    /**
     * 提供给网络层接收数据的api
     * @param ethernetFrame 发送至网络层的数据
     */
    public void sendToNetworkLayer(EthernetFrame ethernetFrame){
        this.resolveToIPv4Packet(ethernetFrame);
        //todo 调用上层数据接口

    }

    /**
     * 提供给网络层发送数据的api
     * @param packet 网络层传来的数据
     */
    public void receiveFromNetworkLayer(IPv4Packet packet) {
        EthernetFrame ethernetFrame = this.resolveToEhernetFrame(packet);
        this.buffer.add(ethernetFrame);
    }

    /**
     *将缓存区中的帧发送至链路
     */
    public void sendToLink() {
        if (this.link.isIdled()&&this.messageContorller.isAllowedTransfer()) {
            for (EthernetFrame ethernetFrame : buffer) {
                boolean occuredCollision = !this.link.receiveFromAdapter(ethernetFrame);
                if (occuredCollision) {
                    this.collisionCounter++;
                    this.messageContorller.pause();
                    break;
                } else {
                    this.messageContorller.reset();
                    this.buffer.remove(ethernetFrame);
                    this.collisionCounter = 0;
                }
            }
        }
    }

    /**
     * 提供给链路传入数据的api
     * @param ethernetFrame 从链路获取的帧
     */
    public void receiveFromLink(EthernetFrame ethernetFrame) {
        if (ethernetFrame.getTargetMAC() == this.MAC) {
            if (this.check(ethernetFrame)) {
                this.buffer.add(ethernetFrame);
            }
        }
    }

    private void joinLink(PhisicalLink link) {
        this.link = link;
        this.link.join(this);
    }

    private IPv4Packet resolveToIPv4Packet(EthernetFrame ethernetFrame){
        //todo 将以太帧转化为IPv4的包
        return null;
    }

    private EthernetFrame resolveToEhernetFrame(IPv4Packet packet){
        EthernetFrame ethernetFrame = new EthernetFrame();
        ethernetFrame.setSourceMAC(this.MAC);
        ethernetFrame.setTargetMAC(this.arpTable.getMACByIP(packet.getTargetIP()));
        ethernetFrame.setPayload(packet.toString());
        ethernetFrame.setType(0x0800);
        ethernetFrame.setCRC(this.generateCRC(ethernetFrame));
        return ethernetFrame;
    }

    private boolean check(EthernetFrame ethernetFrame) {
        return this.crcTool.check(ethernetFrame.getPayload(), ethernetFrame.getCRC());
    }

    private long generateCRC(EthernetFrame ethernetFrame) {
        return this.crcTool.generateCRC(ethernetFrame.getPayload());
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
