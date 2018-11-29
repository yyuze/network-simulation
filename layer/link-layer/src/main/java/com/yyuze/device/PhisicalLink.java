package com.yyuze.device;

import com.yyuze.pkg.EthernetFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */

public class PhisicalLink {

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

    private Random random;

    private long start;

    private long end;

    private BitTransferModel transferModel;

    public PhisicalLink() {
        this.MAC2PositionMap = new HashMap<>();
        this.position2DevicesMap = new HashMap<>();
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

        private BitTransferModel(EthernetFrame frame){
            this.coefficient = this.generateTransferCoefficient(frame);
            this.startPosition = MAC2PositionMap.get(frame.getSourceMAC());
            this.startTime = (float) System.nanoTime()/1000;
            this.transferDelay = ((float) frame.toString().length() * 8 / (float) PhisicalLink.BANDWIDTH)*1000000;
        }

        /**
         * 传输系数 = 传输距离/传输时间
         * @param ethernetFrame 被传输的帧
         * @return 传输系数
         */
        private float generateTransferCoefficient(EthernetFrame ethernetFrame) {
            float linkLength = end - start;
            float transferDelay = ((float) ethernetFrame.toString().length() * 8 / (float) PhisicalLink.BANDWIDTH)*1000000;
            return linkLength/transferDelay;
        }

        /**
         * 获取最后一个bit到达某个位置时所处的时间
         * @param position 位置
         * @return 到达某个位置时的时间
         */
        private float getTimeByPosition(long position){
            return Math.abs(position - this.startPosition)/this.coefficient+this.startTime+this.transferDelay;
        }

        /**
         * 碰撞预测原理：
         * 与另一个帧传输模型作比较：
         * 如果本模型边界值均小于另一个模型，则该模型与另一个模型呈 “V” 型包围状，不会发生碰撞
         * @param anotherModel 需要预测碰撞的另一个模型
         * @return 是否会发生碰撞
         */
        private boolean forecastCollision(BitTransferModel anotherModel){
            float startBoard = this.getTimeByPosition(this.startPosition);
            float endBoardToHead = this.getTimeByPosition(start);
            float endBoardToTail = this.getTimeByPosition(end);

            boolean startBoardCheck = anotherModel.getTimeByPosition(anotherModel.startPosition)>startBoard;
            boolean endBoardToHeadCheck = anotherModel.getTimeByPosition(start)>endBoardToHead;
            boolean endBoardToTailCheck = anotherModel.getTimeByPosition(end)>endBoardToTail;
            return startBoardCheck&&endBoardToHeadCheck&&endBoardToTailCheck;
        }

    }

    /**
     * 模拟现实中的设备随机接入
     * 每一个设备有一个一维坐标position
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
     * 提供给NetworkAdapter调用传输数据的api
     * @param ethernetFrame 需要传输的帧
     */
    public void receiveFromAdapter(EthernetFrame ethernetFrame) {
        /**
         * 由于抽象了物理层，因此此处不做其他处理，直接广播该帧
         */
        this.boardcastFrameInLink(ethernetFrame);
    }

    /**
     * 向链路上广播一个帧
     * 模拟帧在链路上向链路两端扩散广播的过程
     * @param ethernetFrame 被广播的帧
     */
    private void boardcastFrameInLink(EthernetFrame ethernetFrame) {
        this.transferModel = new BitTransferModel(ethernetFrame);
        Long position = this.MAC2PositionMap.get(ethernetFrame.getSourceMAC());
        Long toHeadIndex = position-1;
        Long toTailIndex = position+1;
        while (toHeadIndex >= this.start || toTailIndex <= this.end) {
            if (this.MAC2PositionMap.containsValue(toHeadIndex)) {
                ArrayList<NetworkAdapter> deviceList = this.position2DevicesMap.get(toHeadIndex);
                for(NetworkAdapter device : deviceList){
                    device.receiveFromLink(ethernetFrame);
                }
            }
            if (this.MAC2PositionMap.containsValue(toTailIndex)) {
                ArrayList<NetworkAdapter> deviceList = this.position2DevicesMap.get(toHeadIndex);
                for(NetworkAdapter device : deviceList){
                    device.receiveFromLink(ethernetFrame);
                }
            }
            toTailIndex++;
            toHeadIndex--;
        }
    }

    /**
     * 提供给NetworkAdapter监听链路状态的api
     * @return 链路的状态
     */
    public boolean willOccurCollision(EthernetFrame frame) {
        return this.transferModel.forecastCollision(new BitTransferModel(frame));
    }

}