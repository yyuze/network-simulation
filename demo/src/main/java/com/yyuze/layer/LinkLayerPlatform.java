package com.yyuze.layer;

import com.yyuze.EmptyLinkLayerPlatform;
import com.yyuze.anno.component.LinkInit;
import com.yyuze.anno.platform.Layer;
import com.yyuze.component.PhisicalLink;
import com.yyuze.enums.LayerType;

/**
 * Author: yyuze
 * Time: 2018-12-07
 */

/**
 * 在该类中直接定义链路层的硬件组成
 */
@Layer(LayerType.LINK)
public class LinkLayerPlatform extends EmptyLinkLayerPlatform {

    @LinkInit(serial = 0x00000001, MACs = {0x10000000, 0x10000001, 0x10000002}, switchs = {0x20000001, 0x20000002})
    private PhisicalLink link1;

    @LinkInit(serial = 0x00000002, MACs = {0x10000003, 0x10000004, 0x10000005}, switchs = {0x20000002, 0x20000003})
    private PhisicalLink link2;

    @LinkInit(serial = 0x00000003, MACs = {0x10000006, 0x10000007, 0x10000008}, switchs = {0x20000003, 0x20000001})
    private PhisicalLink link3;

}
