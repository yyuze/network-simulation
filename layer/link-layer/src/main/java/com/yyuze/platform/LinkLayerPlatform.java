package com.yyuze.platform;

import com.yyuze.invoker.command.anno.Command;
import com.yyuze.device.PhisicalLink;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.CommandEnum;
import com.yyuze.enums.LayerType;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.platform.anno.Layer;
import com.yyuze.platform.anno.Link;
import com.yyuze.tool.CRC;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

@Layer(LayerType.LINK)
public class LinkLayerPlatform implements Assembleable {

    @Link(serial = 0x00000001,MACs = {0x10000000,0x10000001,0x10000002},switchs = {0x20000001,0x20000002})
    private PhisicalLink link1;

    @Link(serial = 0x00000002,MACs = {0x10000003,0x10000004,0x10000005},switchs = {0x20000002,0x20000003})
    private PhisicalLink link2;

    @Link(serial = 0x00000003,MACs = {0x10000006,0x10000007,0x10000008},switchs = {0x20000003,0x20000001})
    private PhisicalLink link3;

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
                if(mac==source){
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
