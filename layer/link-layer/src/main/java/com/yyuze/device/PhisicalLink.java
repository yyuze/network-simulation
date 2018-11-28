package com.yyuze.device;

import com.yyuze.pkg.EthernetFrame;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */

public class PhisicalLink {

    //bandwidth = 10Mbps = 10*10^6 bit/s = 10^7/8 byte/s = 1250000 byte/s
    public static long BANDWIDTH = 1250000L;

    private HashMap<Point, NetworkAdapter> devices;

    private Random random;

    public PhisicalLink() {
        this.devices = new HashMap<>();
        this.random = new Random();
    }

    /**
     * 模拟现实中的设备随机接入
     * 用坐标（x，y）定位每一个设备
     * @param networkAdapter 接入的设备
     */
    public void join(NetworkAdapter networkAdapter) {
        Point coordernate = new Point(this.random.nextInt(), this.random.nextInt());
        if (this.devices.containsKey(coordernate)) {
            this.join(networkAdapter);
        } else {
            this.devices.put(coordernate, networkAdapter);
        }
    }

    /**
     * 提供给NetworkAdapter调用传输数据的api
     * @param ethernetFrame 需要传输的帧
     * @return 是否传输成功
     */
    public boolean receiveFromAdapter(EthernetFrame ethernetFrame) {
        this.boardcastFrameInLink(ethernetFrame);
        return true;
    }

    /**
     * 向链路上广播一个帧
     * @param ethernetFrame 被广播的帧
     */
    private void boardcastFrameInLink(EthernetFrame ethernetFrame) {
        this.devices.forEach((point, networkAdapter) -> {
            if (networkAdapter.MAC != ethernetFrame.getSourceMAC()) {
                networkAdapter.receiveFromLink(ethernetFrame);
            }
        });
    }

    /**
     * 提供给NetworkAdapter监听链路空闲状态的api
     * @return 链路的状态
     */
    public boolean isIdled(){
        //todo
        return true;
    }

}
