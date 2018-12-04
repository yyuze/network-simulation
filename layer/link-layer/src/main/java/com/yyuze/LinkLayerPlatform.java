package com.yyuze;

import com.yyuze.anno.Command;
import com.yyuze.anno.Link;
import com.yyuze.anno.Platform;
import com.yyuze.device.NetworkAdapter;
import com.yyuze.device.PhisicalLink;
import com.yyuze.device.Router;
import com.yyuze.device.Switch;
import com.yyuze.enable.DownwardTransmitable;
import com.yyuze.enable.UpwardTransmitable;
import com.yyuze.enums.CommandEnum;
import com.yyuze.enums.LayerType;
import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.packet.IPv4Packet;
import com.yyuze.table.ARPTable;
import com.yyuze.tool.CRC;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Platform(LayerType.LINK)
public class LinkLayerPlatform implements DownwardTransmitable<IPv4Packet>, UpwardTransmitable<EthernetFrame> {

    @Link(serial = 0x00000001,MACs = {0x10000000,0x10000001,0x10000002},switchs = {0x20000001,0x20000002})
    PhisicalLink link1;

    @Link(serial = 0x00000002,MACs = {0x10000003,0x10000004,0x10000005},switchs = {0x20000002,0x20000003})
    PhisicalLink link2;

    @Link(serial = 0x00000003,MACs = {0x10000006,0x10000007,0x10000008},switchs = {0x20000003,0x20000001})
    PhisicalLink link3;

    /**
     * 模拟分布式的ARP工具，Key是设备MAC地址
     */
    HashMap<Long, ARPTable> arptools;

    HashMap<Long, NetworkAdapter> adapters;

    HashMap<Long, Router> routers;


    /**
     * 提供给命令行调用
     * @param content
     */
    @Command(CommandEnum.link_rcv)
    public void receiveString(String content){
        String[] lines = content.split("\n");
        long source = Long.getLong(lines[0]);
        long target = Long.getLong(lines[1]);
        String payload = lines[2];
        EthernetFrame frame = new EthernetFrame();
        frame.setSourceMAC(source);
        frame.setTargetMAC(target);
        frame.setPayload(payload);
        frame.setType(0x0800);
        frame.setCRC(new CRC().generateCRC(payload));
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields){
            Link anno = field.getAnnotation(Link.class);
            long macs[] = anno.MACs();
            for(long mac:macs){
                if(mac==target){
                    try {
                        PhisicalLink.class.getDeclaredMethod("receive",EthernetFrame.class).invoke(field,frame);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void tansmitToLower(long MAC,IPv4Packet packet) {
        EthernetFrame frame = new EthernetFrame();
        frame.setPayload(packet.toString());
        frame.setTargetMAC(this.arptools.get(MAC).getMACByIP(packet.getTargetIP()));
        try {
            this.adapters.get(MAC).receiveFromUpper(frame);
        } catch (Exception e) {
            e.printStackTrace();
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

    private IPv4Packet convertFrameToIpPacket(EthernetFrame frame){
        //todo
        return null;
    }
}
