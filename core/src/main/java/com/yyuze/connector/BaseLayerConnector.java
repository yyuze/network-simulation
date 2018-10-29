package com.yyuze.connector;

import com.yyuze.pkg.BasePackage;
import com.yyuze.layer.BaseLayer;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-10-30
 * <p>
 * 每层之间的连接通道的基类
 * 负责为层与层之间提供数据传递接口
 * </p>
 */
public abstract class BaseLayerConnector<PKG extends BasePackage, LAYER extends BaseLayer> {

    /**
     * 通道的上层
     */
    protected LAYER lowerLayer;

    /**
     * 通道的下层
     */
    protected LAYER upperLayer;

    public LAYER getLowerLayer() {
        return lowerLayer;
    }

    public void setLowerLayer(LAYER lowerLayer) {
        this.lowerLayer = lowerLayer;
    }

    public LAYER getUpperLayer() {
        return upperLayer;
    }

    public void setUpperLayer(LAYER upperLayer) {
        this.upperLayer = upperLayer;
    }

    /**
     * 将数据上发至上层的缓存区
     *
     * @param pkgs 下层协议封装的数据
     */
    public void sendToUpper(ArrayList<PKG> pkgs) {
        for (PKG pgk : pkgs) {
            pgk.setLayer(this.lowerLayer);
        }
        this.upperLayer.pushToBuffer(pkgs);
    }

    /**
     * 将数据发至下层的缓存区
     *
     * @param pkgs 上层协议封装的数据
     */
    public void sendToLower(ArrayList<PKG> pkgs) {
        for (PKG pkg : pkgs) {
            pkg.setLayer(this.upperLayer);
        }
        this.lowerLayer.pushToBuffer(pkgs);
    }
}
