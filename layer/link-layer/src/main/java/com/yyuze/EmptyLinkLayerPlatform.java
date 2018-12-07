package com.yyuze;

import com.yyuze.anno.system.Command;
import com.yyuze.builder.BaseBuilder;
import com.yyuze.component.NetworkAdapter;
import com.yyuze.component.PhisicalLink;
import com.yyuze.component.Switch;
import com.yyuze.enable.Assembleable;
import com.yyuze.enums.CommandEnum;
import com.yyuze.exception.PortFullOccupiedException;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.anno.component.Link;
import com.yyuze.tool.CRC;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */

public abstract class EmptyLinkLayerPlatform implements Assembleable {

    static public class Builder extends BaseBuilder {

        public Builder() {
            super();
        }

        @Override
        public Assembleable buildRuntimePlatform(Class<? extends Assembleable> clz) {
            try {
                Assembleable platform = clz.getConstructor().newInstance();
                this.connectLinksToPlatform(platform);
                this.instances.add(platform);
                return platform;
            } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void connectLinksToPlatform(Assembleable platform) throws IllegalAccessException {
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
