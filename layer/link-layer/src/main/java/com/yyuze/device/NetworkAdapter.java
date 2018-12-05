package com.yyuze.device;

import com.yyuze.platform.anno.Layer;
import com.yyuze.invoker.schedule.anno.Schedule;
import com.yyuze.enums.LayerType;
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

@Layer(LayerType.LINK)
public class NetworkAdapter extends BaseDevice<EthernetFrame>{

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
     * 提供给runtime平台调用构造基础网络的api
     *
     * @param link
     */
    public void joinLink(PhisicalLink link) {
        this.link = link;
        this.link.join(this);
    }

    /**
     * 该API由Runtime平台轮询调用
     * 将需要往上层传递的帧发送到平台中
     */
    @Schedule(period = 1000)
    @Override
    protected void sendToUpper() {
        for (EthernetFrame frame:this.toUpperBuffer){
            this.toUpperBuffer.addDeleteSignFor(frame);
            this.platform.tansmitToUpper(this.MAC,frame);
        }
        this.toUpperBuffer.clean();
    }

    /**
     * 该API由Runtime平台轮询调用
     * 将缓存区中的帧发送至链路
     */
    @Schedule(period = 1000)
    @Override
    protected void sendToLower() {
        if (this.activityContorller.isAllowedTransfer()) {
            for (EthernetFrame frame : this.toLowerBuffer) {
                this.toLowerBuffer.addDeleteSignFor(frame);
                if (this.link.willOccurCollision(frame)) {
                    this.collisionCounter++;
                    this.activityContorller.pause(this.collisionCounter);
                    this.toLowerBuffer.removeDeleteSignFor(frame);
                    break;
                } else {
                    /**
                     * frame的 payload 和 targetMAC 通过平台设置
                     */
                    frame.setSourceMAC(this.MAC);
                    frame.setType(0x0800);
                    frame.setCRC(this.generateCRC(frame));
                    this.link.receive(frame);
                    this.activityContorller.reset();
                    this.collisionCounter = 0;
                }
            }
         this.toLowerBuffer.clean();
        }
    }

    /**
     * 该API由Runtime平台通信时调用
     * @param packet
     * @param <T>
     */
    @Override
    public <T extends BasePacket> void receiveFromUpper(T packet) throws PacketTypeException {
        if(!packet.getClass().equals(EthernetFrame.class)){
            throw new PacketTypeException();
        }
        EthernetFrame frame = (EthernetFrame)packet;
        this.toLowerBuffer.add(frame);
    }

    /**
     * 提供给链路向适配器发送数据时调用的api
     *
     * @param packet 从链路获取的帧
     */
    @Override
    protected <T extends BasePacket> void receiveFromLower(T packet) throws PacketTypeException {
        if(!packet.getClass().equals(EthernetFrame.class)){
            throw new PacketTypeException();
        }
        EthernetFrame frame = (EthernetFrame)packet;
        if (frame.getTargetMAC() == this.MAC || frame.getTargetMAC() == 0xffffffff) {
            if (this.check(frame)) {
                this.toUpperBuffer.add(frame);
            }
        }
    }
}
