package com.yyuze.device;

import com.yyuze.anno.Platform;
import com.yyuze.enums.LayerType;
import com.yyuze.packet.EthernetFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */

/**
 * 基础以太网链路
 */

@Platform(LayerType.LINK)
public class PhisicalLink {

    public long serial;

    //bandwidth = 10Mbps = 10*10^6 bit/s = 10^7/8 byte/s = 1250000 byte/s
    public static long BANDWIDTH = 1250000L;

    /**
     * MAC => position
     * 用MAC地址索引设备位置的MAP
     */
    private HashMap<Long, Long> MAC2PositionMap;

    /**
     * position => position2DevicesMap
     * 用位置坐标索引在该位置的设备列表
     */
    private HashMap<Long, ArrayList<NetworkAdapter>> position2DevicesMap;

    /**
     * 链路接入的交换机
     */
    private ArrayList<Switch> switches;

    /**
     * 随机数产生器
     */
    private Random random;

    /**
     * 链路起点
     */
    private long start;

    /**
     * 链路终点
     */
    private long end;

    /**
     * 链路传输帧时的传输模型
     */
    private BitTransferModel transferModel;

    public PhisicalLink(long serial) {
        this.serial = serial;
        this.MAC2PositionMap = new HashMap<>();
        this.position2DevicesMap = new HashMap<>();
        this.switches = new ArrayList<>();
        this.random = new Random();
        this.start = 0L;
        this.end = 0L;
    }

    /**
     * 比特传播的时间与距离关系模型
     */
    private class BitTransferModel {

        private float coefficient;

        private long startPosition;

        private float startTime;

        private float transferDelay;

        private BitTransferModel(EthernetFrame frame) {
            this.coefficient = this.generateTransferCoefficient(frame);
            this.startPosition = MAC2PositionMap.get(frame.getSourceMAC());
            this.startTime = (float) System.nanoTime() / 1000;
            this.transferDelay = ((float) frame.toString().length() * 8 / (float) PhisicalLink.BANDWIDTH) * 1000000;
        }

        /**
         * 传输系数 = 传输距离/传输时间
         *
         * @param ethernetFrame 被传输的帧
         * @return 传输系数
         */
        private float generateTransferCoefficient(EthernetFrame ethernetFrame) {
            float linkLength = end - start;
            float transferDelay = ((float) ethernetFrame.toString().length() * 8 / (float) PhisicalLink.BANDWIDTH) * 1000000;
            return linkLength / transferDelay;
        }

        /**
         * 获取最后一个bit到达某个位置时所处的时间
         *
         * @param position 位置
         * @return 到达某个位置时的时间
         */
        private float getTimeByPosition(long position) {
            return Math.abs(position - this.startPosition) / this.coefficient + this.startTime + this.transferDelay;
        }

        /**
         * 碰撞预测原理：
         * 与另一个帧传输模型作比较：
         * 如果本模型边界值均小于另一个模型，则该模型与另一个模型呈 “V” 型包围状，不会发生碰撞
         *
         * @param anotherModel 需要预测碰撞的另一个模型
         * @return 是否会发生碰撞
         */
        private boolean forecastCollision(BitTransferModel anotherModel) {
            float startBoard = this.getTimeByPosition(this.startPosition);
            float endBoardToHead = this.getTimeByPosition(start);
            float endBoardToTail = this.getTimeByPosition(end);

            boolean startBoardCheck = anotherModel.getTimeByPosition(anotherModel.startPosition) > startBoard;
            boolean endBoardToHeadCheck = anotherModel.getTimeByPosition(start) > endBoardToHead;
            boolean endBoardToTailCheck = anotherModel.getTimeByPosition(end) > endBoardToTail;
            return startBoardCheck && endBoardToHeadCheck && endBoardToTailCheck;
        }

    }

    /**
     * 模拟现适配器接入
     * 每一个设备有一个一维坐标position
     *
     * @param device 接入的设备
     */
    public void join(NetworkAdapter device) {
        Long position = this.random.nextLong();
        if (!this.position2DevicesMap.containsKey(position)) {
            this.position2DevicesMap.put(position, new ArrayList<>());
        }
        this.position2DevicesMap.get(position).add(device);
        this.MAC2PositionMap.put(device.MAC, position);
        if (position < start) {
            start = position;
        }
        if (position > end) {
            end = position;
        }
    }

    /**
     * 模拟交换机接入
     *
     * @param device
     */
    public void join(Switch device) {
        this.switches.add(device);
    }

    private void sendFrameTo(EthernetFrame frame, long position) {
        if (this.MAC2PositionMap.containsValue(position)) {
            ArrayList<NetworkAdapter> deviceList = this.position2DevicesMap.get(position);
            for (NetworkAdapter device : deviceList) {
                try {
                    device.receive(frame);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向链路上广播一个帧
     * 模拟帧在链路上向链路两端扩散广播的过程
     * 最后向链路上的交换机广播
     *
     * @param ethernetFrame 被广播的帧
     */
    private void boardcastFrameInLink(EthernetFrame ethernetFrame) {
        this.transferModel = new BitTransferModel(ethernetFrame);
        Long position = this.MAC2PositionMap.get(ethernetFrame.getSourceMAC());
        Long toHeadIndex = position - 1;
        Long toTailIndex = position + 1;
        while (toHeadIndex >= this.start || toTailIndex <= this.end) {
            this.sendFrameTo(ethernetFrame, toHeadIndex);
            this.sendFrameTo(ethernetFrame, toTailIndex);
            toTailIndex++;
            toHeadIndex--;
        }
        for (Switch device : this.switches) {
            device.receive(ethernetFrame);
        }
    }

    /**
     * 监听链路状态
     *
     * @return 链路的状态
     */
    public boolean willOccurCollision(EthernetFrame frame) {
        return this.transferModel.forecastCollision(new BitTransferModel(frame));
    }

    /**
     * 查询链路中是否含有某个MAC设备
     *
     * @param MAC
     * @return
     */
    public boolean containsMAC(long MAC) {
        return this.MAC2PositionMap.containsKey(MAC);
    }

    /**
     * 提供给链路上其他设备传输数据的api
     * 由于抽象了物理层，因此此处不做其他处理，直接广播数据帧
     *
     * @param ethernetFrame 需要传输的帧
     */
    public void receive(EthernetFrame ethernetFrame) {
        this.boardcastFrameInLink(ethernetFrame);
    }
}
