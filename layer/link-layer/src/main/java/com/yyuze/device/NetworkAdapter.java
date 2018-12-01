package com.yyuze.device;

import com.yyuze.pkg.IPv4Packet;
import com.yyuze.pkg.EthernetFrame;
import com.yyuze.table.AddressResolutionProtocolTable;
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
public class NetworkAdapter {

    private CRC crcTool;

    public long MAC;

    private PhisicalLink link;

    private ArrayList<EthernetFrame> buffer;

    private int collisionCounter;

    private ActivityContorller activityContorller;

    private AddressResolutionProtocolTable arpTable;

    public NetworkAdapter(long MAC) {
        this.crcTool = new CRC();
        this.MAC = MAC;
        this.buffer = new ArrayList<>();
        this.collisionCounter = 0;
        this.activityContorller = new ActivityContorller();
        //todo arp
    }

    private IPv4Packet resolveToIPv4Packet(EthernetFrame ethernetFrame) {
        //todo 将以太帧转化为IPv4的包
        return null;
    }

    private EthernetFrame resolveToEhernetFrame(IPv4Packet packet) {
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

    /**
     * 提供给链路向适配器发送数据时调用的api
     *
     * @param ethernetFrame 从链路获取的帧
     */
    public void receiveFromLink(EthernetFrame ethernetFrame) {
        if (ethernetFrame.getTargetMAC() == this.MAC) {
            if (this.check(ethernetFrame)) {
                this.buffer.add(ethernetFrame);
            }
        }
    }

    /**
     * 提供给网络层接收数据的api
     *
     * @param ethernetFrame 发送至网络层的数据
     */
    public void sendToNetworkLayer(EthernetFrame ethernetFrame) {
        this.resolveToIPv4Packet(ethernetFrame);
        //todo 调用上层数据接口

    }

    /**
     * 提供给网络层发送数据的api
     *
     * @param packet 网络层传来的数据
     */
    public void receiveFromNetworkLayer(IPv4Packet packet) {
        EthernetFrame ethernetFrame = this.resolveToEhernetFrame(packet);
        this.buffer.add(ethernetFrame);
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
