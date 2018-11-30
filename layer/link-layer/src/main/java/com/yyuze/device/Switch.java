package com.yyuze.device;

import com.yyuze.pkg.EthernetFrame;
import com.yyuze.table.SwitchTable;
import com.yyuze.tool.ActivityContorller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-30
 */

/**
 * 二层交换机
 */
public class Switch {

    /**
     * 可接入的端口数
     */
    private static byte PORT_AMOUNT = 127;

    /**
     * 交换机连接的链路
     */
    private HashMap<Integer, PhisicalLink> links;

    /**
     * 交换机的转发表
     */
    private SwitchTable switchTable;

    /**
     * 交换机缓存
     */
    private ArrayList<EthernetFrame> buffer;

    /**
     * 随机数生成器
     */
    private Random random;

    /**
     * 每一个端口都有一个转发碰撞控制器
     */
    private HashMap<Integer, ActivityContorller> activityContorllers;

    /**
     * 端口转发时发生碰撞的次数
     */
    private int[] collisionCount;

    public Switch(){
        this.links = new HashMap<>();
        this.switchTable = new SwitchTable();
        this.buffer = new ArrayList<>();
        this.random = new Random();
        this.activityContorllers = new HashMap<>();
        this.collisionCount = new int[128];
    }

    public void joinLink(PhisicalLink link) throws Exception {
        if (this.links.size() > Switch.PORT_AMOUNT) {
            throw new Exception() {
                @Override
                public String getMessage() {
                    return "交换机端口已满,无法接入";
                }
            };
        }
        int accessPort = this.random.nextInt(Switch.PORT_AMOUNT);
        if (!this.links.containsKey(accessPort)) {
            this.links.put(accessPort, link);
            this.activityContorllers.put(accessPort,new ActivityContorller());
            link.join(this);
        } else {
            this.joinLink(link);
        }
    }

    public void receive(EthernetFrame frame) {
        this.buffer.add(frame);
    }

    /**
     * 向指定端口转发帧
     * @param targetPort
     * @param frame
     */
    private void transferToPort(int targetPort, EthernetFrame frame) {
        ActivityContorller transferController = this.activityContorllers.get(targetPort);
        if (transferController.isAllowedTransfer()) {
            PhisicalLink link = this.links.get(targetPort);
            if (link.willOccurCollision(frame)) {
                this.collisionCount[targetPort]++;
                transferController.pause(this.collisionCount[targetPort]);
            } else {
                link.receive(frame);
                this.buffer.remove(frame);
                this.collisionCount[targetPort] = 0;
                transferController.reset();
            }
        }
    }

    /**
     * 由Runtime轮询调用
     * 如果目标MAC在源端口对应的链路上，则不转发
     * 如果目标MAC所在链路对应的端口记录不存在，则向其他端口广播
     * 如果目标MAC在其他端口对应的链路，则向那个端口转发
     */
    public void transfer() {
        for (EthernetFrame frame : this.buffer) {
            int targetPort = this.switchTable.getPortByMAC(frame.getTargetMAC());
            int sourcePort = this.switchTable.getPortByMAC(frame.getSourceMAC());
            if (targetPort != sourcePort) {
                if (targetPort != -1) {
                    this.transferToPort(targetPort, frame);
                } else {
                    this.links.forEach((port, link) -> {
                        if (!link.containsMAC(frame.getSourceMAC())) {
                            this.transferToPort(port, frame);
                        }
                    });
                }
            }else{
                this.buffer.remove(frame);
            }
        }
    }
}
