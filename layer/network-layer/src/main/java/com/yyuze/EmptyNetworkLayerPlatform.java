package com.yyuze;

import com.yyuze.anno.component.RouterInit;
import com.yyuze.component.Router;
import com.yyuze.enable.Assembleable;

/**
 * Author: yyuze
 * Time: 2018-12-16
 */
public abstract class EmptyNetworkLayerPlatform implements Assembleable {

    @RouterInit(serial = 0x00000001,MACs = {})
    private Router router1;

    @RouterInit(serial = 0x00000002,MACs = {})
    private Router router2;

    @RouterInit(serial = 0x00000003,MACs = {})
    private Router router3;

    @RouterInit(serial = 0x00000004,MACs = {})
    private Router router4;

}
