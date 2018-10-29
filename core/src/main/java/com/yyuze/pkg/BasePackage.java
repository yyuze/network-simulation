package com.yyuze.pkg;

import com.yyuze.layer.BaseLayer;

/**
 * Author: yyuze
 * Time: 2018-10-30
 * <p>
 * 每一层使用的协议数据包的基类
 * </p>
 */
public abstract class BasePackage<LAYER extends BaseLayer> {

    protected LAYER layer;

    protected String data;

    public LAYER getLayer() {
        return layer;
    }

    public void setLayer(LAYER layer) {
        this.layer = layer;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
