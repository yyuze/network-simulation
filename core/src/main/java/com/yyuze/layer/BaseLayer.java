package com.yyuze.layer;

import com.yyuze.connector.BaseLayerConnector;
import com.yyuze.pkg.BasePackage;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Author: yyuze
 * Time: 2018-10-30
 * <p>
 * 层的基类
 * 负责每一层的具体的协议事务
 * 通过通道通信
 * </p>
 */
public abstract class BaseLayer<PKG extends BasePackage, CONN extends BaseLayerConnector> extends Thread {

    protected Logger logger;

    /**
     * 向上传递的数据包的缓存
     */
    protected ArrayList<PKG> upperBuffer;

    /**
     * 向下传递的数据包的缓存
     */
    protected ArrayList<PKG> lowerBuffer;

    /**
     * 与下面一层的连接通道
     */
    protected CONN toUpper;

    /**
     * 与上面一层的连接通道
     */
    protected CONN toLower;

    /**
     * @param pkg 从下层传入的包
     * @return 按照上层协议解析的包
     */
    abstract PKG resolveToUpper(PKG pkg);

    /**
     * @param pkg 从上层传入的包
     * @return 按照下层协议解析的包
     */
    abstract PKG resolveToLower(PKG pkg);

    public void pushToBuffer(ArrayList<PKG> pkgs) {
        for (PKG pkg : pkgs) {
            this.logger.info(this.getClass().getName() + " received: " + pkg.getData());
            boolean fromLower = toLower.getLowerLayer() == pkg.getLayer();
            if (fromLower) {
                this.upperBuffer.add(this.resolveToUpper(pkg));
            } else {
                this.lowerBuffer.add(this.resolveToLower(pkg));
            }
        }
    }

    public void flush() {
        this.toUpper.sendToUpper(this.upperBuffer);
        this.toLower.sendToLower(this.lowerBuffer);
    }
}
