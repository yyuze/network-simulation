package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.builder.BaseBuilder;
import com.yyuze.component.NetworkAdapter;
import com.yyuze.component.PhisicalLink;
import com.yyuze.component.Switch;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.CommandEnum;
import com.yyuze.enums.LayerType;
import com.yyuze.exception.PortFullOccupiedException;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.anno.platform.Layer;
import com.yyuze.anno.component.Link;
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

    static public class Builder extends BaseBuilder {

        public Builder() {
            super();
        }

        @Override
        public LinkLayerPlatform buildRuntimePlatform() {
            LinkLayerPlatform platform = new LinkLayerPlatform();
            try {
                this.connectLinksToPlatform(platform);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            this.instances.add(platform);
            return platform;
        }

        private void connectLinksToPlatform(LinkLayerPlatform platform) throws IllegalAccessException {
            Field[] links = platform.getClass().getFields();
            for (Field link : links) {
                Link anno = link.getAnnotation(Link.class);
                if (anno == null) {
                    continue;
                }
                long serial = anno.serial();
                PhisicalLink linkInstance = new PhisicalLink(serial);
                link.set(platform, linkInstance);
                this.accessMACsToLink(linkInstance, anno.MACs());
                try {
                    this.accessSwitchesToLink(linkInstance, anno.switchs());
                } catch (PortFullOccupiedException e) {
                    e.printStackTrace();
                }
                this.instances.add(platform);
            }
        }

        private void accessMACsToLink(PhisicalLink link, long[] accessedMACs) {
            for (long MAC : accessedMACs) {
                NetworkAdapter device = new NetworkAdapter(MAC);
                device.joinLink(link);
                link.join(device);
                this.instances.add(device);
            }
        }

        private void accessSwitchesToLink(PhisicalLink link, long[] accessedSwitches) throws PortFullOccupiedException {
            for (long serial : accessedSwitches) {
                Switch device = new Switch(serial);
                device.joinLink(link);
                link.join(device);
                this.instances.add(device);
            }
        }
    }

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
                        PhisicalLink.class.getDeclaredMethod("receive",EthernetFrame.class).invoke(field.get(this),frame);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
