package com.yyuze;

import com.yyuze.anno.Action;
import com.yyuze.anno.Link;
import com.yyuze.anno.Platform;
import com.yyuze.device.PhisicalLink;
import com.yyuze.enums.Command;
import com.yyuze.enums.LayerType;
import com.yyuze.packet.BasePacket;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.tool.ARP;
import com.yyuze.tool.CRC;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Platform(LayerType.LINK)
public class LinkLayer extends BasePlatform {

    @Link(serial = 0x00000001,MACs = {0x10000000,0x10000001,0x10000002},switchs = {0x20000001,0x20000002})
    PhisicalLink link1;

    @Link(serial = 0x00000002,MACs = {0x10000003,0x10000004,0x10000005},switchs = {0x20000002,0x20000003})
    PhisicalLink link2;

    @Link(serial = 0x00000003,MACs = {0x10000006,0x10000007,0x10000008},switchs = {0x20000003,0x20000001})
    PhisicalLink link3;

    @Override
    public <T extends BasePacket> void receive(T packet) {
        //todo 对网络层抽象链路层
    }

    @Override
    public <T extends BasePacket> void send(T packet) {
        //todo 对网络层抽象链路层
    }

    /**
     * 提供给命令行调用
     * @param content
     */
    @Action(Command.link_rcv)
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

}
