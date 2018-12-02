package com.yyuze.device;

import com.yyuze.anno.Action;
import com.yyuze.anno.Platform;
import com.yyuze.enums.Command;
import com.yyuze.enums.LayerType;
import com.yyuze.packet.BasePacket;
import com.yyuze.packet.IPv4Packet;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.tool.ARP;
import com.yyuze.tool.ActivityContorller;
import com.yyuze.tool.CRC;

import java.util.ArrayList;


/**
 * Author: yyuze
 * Time: 2018-11-19
 */

/**
 * 网络适配器
 * 数据校验协议：CRC
 * 多路访问协议：CSMA/CD
 */

@Platform(LayerType.LINK)
public class NetworkAdapter extends BaseDevice{

    private CRC crcTool;

    public long MAC;

    private ArrayList<EthernetFrame> buffer;

    private PhisicalLink link;

    private int collisionCounter;

    private ActivityContorller activityContorller;

    public NetworkAdapter(long MAC) {
        this.crcTool = new CRC();
        this.MAC = MAC;
        this.buffer = new ArrayList<>();
        this.collisionCounter = 0;
        this.activityContorller = new ActivityContorller();
    }

    private EthernetFrame resolveToEhernetFrame(IPv4Packet packet) {
        EthernetFrame ethernetFrame = new EthernetFrame();
        ethernetFrame.setSourceMAC(this.MAC);
        ethernetFrame.setTargetMAC(ARP.getMACByIP(packet.getTargetIP()));
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

    /**
     * 提供给链路向适配器发送数据时调用的api
     *
     * @param packet 从链路获取的帧
     */
    @Override
    public <T extends BasePacket> void receive(T packet) throws Exception {
        if(!packet.getClass().equals(EthernetFrame.class)){
            throw new Exception(){
                @Override
                public String getMessage() {
                    return "wrong packet type";
                }
            };
        }
        EthernetFrame frame = (EthernetFrame)packet;
        if (frame.getTargetMAC() == this.MAC || frame.getTargetMAC() == 0xffffffff) {
            if (this.check(frame)) {
                this.buffer.add(frame);
            }
        }
    }

    /**
     * 该API由Runtime平台轮询调用
     * 将缓存区中的帧发送至链路
     */
    public void sendToLink() {
        if (this.activityContorller.isAllowedTransfer()) {
            for (EthernetFrame frame : buffer) {
                if (this.link.willOccurCollision(frame)) {
                    this.collisionCounter++;
                    this.activityContorller.pause(this.collisionCounter);
                    break;
                } else {
                    this.link.receive(frame);
                    this.buffer.remove(frame);
                    this.activityContorller.reset();
                    this.collisionCounter = 0;
                }
            }
        }
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

}
