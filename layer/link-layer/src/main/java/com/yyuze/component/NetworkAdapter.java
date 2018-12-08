package com.yyuze.component;

import com.yyuze.anno.system.Schedule;
import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.BasePacket;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.tool.ActivityContorller;
import com.yyuze.tool.Buffer;
import com.yyuze.tool.CRC;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */

/**
 * 网络适配器
 * 数据校验协议：CRC
 * 多路访问协议：CSMA/CD
 */

public class NetworkAdapter extends BaseDevice<EthernetFrame> {

    private CRC crcTool;

    public long MAC;

    private Buffer<EthernetFrame> toUpperBuffer;

    private Buffer<EthernetFrame> toLowerBuffer;

    private PhisicalLink link;

    private int collisionCounter;

    private ActivityContorller activityContorller;

    public NetworkAdapter(long MAC) {
        this.crcTool = new CRC();
        this.MAC = MAC;
        this.toUpperBuffer = new Buffer<>();
        this.toLowerBuffer = new Buffer<>();
        this.collisionCounter = 0;
        this.activityContorller = new ActivityContorller();
    }

    private boolean check(EthernetFrame ethernetFrame) {
        return this.crcTool.check(ethernetFrame.getPayload(), ethernetFrame.getCRC());
    }

    private long generateCRC(EthernetFrame ethernetFrame) {
        return this.crcTool.generateCRC(ethernetFrame.getPayload());
    }

    /**
     * 构造基础网络的api
     *
     * @param link
     */
    public void joinLink(PhisicalLink link) {
        this.link = link;
        this.link.join(this);
    }

    /**
     * 轮询调用
     * 向网络层发送帧
     */
    @Schedule(period = 1000)
    @Override
    protected void sendToUpper() {
        for (EthernetFrame frame : this.toUpperBuffer) {
            this.toUpperBuffer.addDeleteSignFor(frame);
            this.bridge.tansmitToUpper(this.MAC, frame);
        }
        this.toUpperBuffer.clean();
    }

    /**
     * 轮询调用
     * 向链路中发送帧
     */
    @Schedule(period = 1000)
    @Override
    protected void sendToLower() {
        if (this.activityContorller.isAllowedTransfer()) {
            for (EthernetFrame frame : this.toLowerBuffer) {
                /**
                 * frame的 payload 和 targetMAC 通过平台设置
                 */
                frame.setSourceMAC(this.MAC);
                frame.setType(0x0800);
                frame.setCRC(this.generateCRC(frame));
                this.toLowerBuffer.addDeleteSignFor(frame);
                if (this.link.willOccurCollision(frame)) {
                    this.collisionCounter++;
                    this.activityContorller.pause(this.collisionCounter);
                    this.toLowerBuffer.removeDeleteSignFor(frame);
                    break;
                } else {
                    this.link.receive(frame);
                    this.activityContorller.reset();
                    this.collisionCounter = 0;
                }
            }
            this.toLowerBuffer.clean();
        }
    }

    /**
     * 从网络层接收帧
     *
     * @param packet 接受的帧
     * @param <T> 数据包的类型
     */
    @Override
    public <T extends BasePacket> void receiveFromUpper(T packet) throws PacketTypeException {
        if (!packet.getClass().equals(EthernetFrame.class)) {
            throw new PacketTypeException();
        }
        EthernetFrame frame = (EthernetFrame) packet;
        this.toLowerBuffer.add(frame);
    }

    /**
     * 从链路中接收帧
     * @param packet 接收的帧
     * @param <T> 数据包类型
     * @throws PacketTypeException
     */
    @Override
    protected <T extends BasePacket> void receiveFromLower(T packet) throws PacketTypeException {
        if (!packet.getClass().equals(EthernetFrame.class)) {
            throw new PacketTypeException();
        }
        EthernetFrame frame = (EthernetFrame) packet;
        if (frame.getTargetMAC() == this.MAC || frame.getTargetMAC() == 0xffffffff) {
            if (this.check(frame)) {
                this.toUpperBuffer.add(frame);
            }
        }
    }

    public long getLinkSerial(){
        return this.link.serial;
    }
}
