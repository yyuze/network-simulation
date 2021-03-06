package com.yyzue.bridge;

import com.yyuze.component.NetworkAdapter;
import com.yyuze.component.Router;
import com.yyuze.enable.DownwardTransmitable;
import com.yyuze.enable.UpwardTransmitable;
import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.packet.Datagram;
import com.yyzue.tool.ARP;


import java.util.HashMap;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public class LinkLayerAndNetworkLayer implements DownwardTransmitable<Datagram>, UpwardTransmitable<EthernetFrame> {


    /**
     * 模拟分布式的ARP工具，Key是设备MAC地址
     */
    HashMap<Long, ARP> arptools;

    HashMap<Long, NetworkAdapter> adapters;

    HashMap<Long, Router> routers;

    @Override
    public void tansmitToLower(long MAC, Datagram packet) {
        EthernetFrame frame = new EthernetFrame();
        frame.setPayload(packet.toString());
        ARP arptool = this.arptools.get(MAC);
        long mac = arptool.getMACByIP(packet.getTargetIP());
        if(mac != -1){
            frame.setTargetMAC(mac);
            try {
                this.adapters.get(MAC).receiveFromUpper(frame);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //todo arp logic
        }

    }

    @Override
    public void tansmitToUpper(long MAC,EthernetFrame packet) {
        try {
            this.routers.get(MAC).receiveFromLower(this.convertFrameToIpPacket(packet));
        } catch (PacketTypeException e) {
            e.printStackTrace();
        }
    }

    private Datagram convertFrameToIpPacket(EthernetFrame frame){
        //todo
        return null;
    }

}
