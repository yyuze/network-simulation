package com.yyuze.tool;

import com.yyuze.component.PhisicalLink;

import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-30
 */

/**
 * 控制传输设备的传输活动的类，若发生碰撞则设备静默一定时间
 * 采用二进制指数回退算法计算静默时间
 */
public class ActivityContorller {

    private long BIT_TIME_512;

    private Random randomTool;

    private long transferAllowTimeStamp;

    public ActivityContorller() {
        this.BIT_TIME_512 = 512000000 / PhisicalLink.BANDWIDTH;
        this.transferAllowTimeStamp = 0;
        this.randomTool = new Random();
    }

    private long getMicrotimeStamp() {
        return System.nanoTime() / 1000;
    }

    private long generateBinaryExponentialBackoff(int collisionCounter) {
        return this.randomTool.nextInt((int) Math.pow(2, collisionCounter)) * this.BIT_TIME_512;
    }

    public void pause(int collisionCounter) {
        this.transferAllowTimeStamp = this.getMicrotimeStamp()+this.generateBinaryExponentialBackoff(collisionCounter);
    }

    public void reset() {
        this.transferAllowTimeStamp = 0;
    }

    public boolean isAllowedTransfer() {
        return this.transferAllowTimeStamp <= this.getMicrotimeStamp();
    }

}