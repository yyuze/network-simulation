package com.yyuze.builder;

import com.yyuze.platform.anno.Link;
import com.yyuze.device.NetworkAdapter;
import com.yyuze.device.PhisicalLink;
import com.yyuze.device.Switch;
import com.yyuze.exception.PortFullOccupiedException;
import com.yyuze.platform.LinkLayerPlatform;

import java.lang.reflect.Field;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public class LinkLayerBuilder extends BaseBuilder{

   public LinkLayerBuilder(){
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
        return platform;
    }

    private void connectLinksToPlatform(LinkLayerPlatform platform) throws IllegalAccessException {
        Field[] links = platform.getClass().getFields();
        for(Field link:links){
            Link anno = link.getAnnotation(Link.class);
            long serial = anno.serial();
            PhisicalLink linkInstance = new PhisicalLink(serial);
            link.set(platform,linkInstance);
            this.accessMACsToLink(linkInstance,anno.MACs());
            try {
                this.accessSwitchesToLink(linkInstance,anno.switchs());
            } catch (PortFullOccupiedException e) {
                e.printStackTrace();
            }
        }
    }

    private void accessMACsToLink(PhisicalLink link,long[] accessedMACs){
        for(long MAC : accessedMACs){
            NetworkAdapter device = new NetworkAdapter(MAC);
            device.joinLink(link);
            link.join(device);
        }
    }

    private void accessSwitchesToLink(PhisicalLink link,long[] accessedSwitches) throws PortFullOccupiedException {
        for(long serial : accessedSwitches){
            Switch device = new Switch(serial);
            device.joinLink(link);
            link.join(device);
        }
    }

}
