package com;

import com.yyuze.VirtualNetworkSimulation;
import com.yyuze.layer.LinkLayerPlatform;

/**
 * Author: yyuze
 * Time: 2018-12-02
 */
public class Starter {

    public static void main(String[] args){
        new VirtualNetworkSimulation(LinkLayerPlatform.class,null,null,null).start();
    }

}
