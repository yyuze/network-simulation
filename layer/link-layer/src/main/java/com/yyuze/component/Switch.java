package com.yyuze.component;

import com.yyuze.anno.system.Schedule;
import com.yyuze.exception.PortFullOccupiedException;
import com.yyuze.packet.EthernetFrame;
import com.yyuze.table.SwitchTable;
import com.yyuze.tool.ActivityContorller;
import com.yyuze.tool.Buffer;

import java.util.HashMap;
import java.util.Random;

/**
 * Author: yyuze
 * Time: 2018-11-30
 */

/**
 * 二层交换机
 * 多路访问协议：CSMA/CD
 */

public class Switch {

    public long serial;

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


    private Buffer<EthernetFrame> buffer;

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

    public Switch(long serial) {
        this.serial = serial;
        this.links = new HashMap<>();
        this.switchTable = new SwitchTable();
        this.buffer = new Buffer<>();
        this.random = new Random();
        this.activityContorllers = new HashMap<>();
        this.collisionCount = new int[128];
    }

    /**
     * 向指定端口转发帧
     *
     * @param targetPort
     * @param frame
     */
    private boolean transferToPort(int targetPort, EthernetFrame frame) {
        ActivityContorller transferController = this.activityContorllers.get(targetPort);
        if (transferController.isAllowedTransfer()) {
            PhisicalLink link = this.links.get(targetPort);
            if (link.willOccurCollision(frame)) {
                this.collisionCount[targetPort]++;
                transferController.pause(this.collisionCount[targetPort]);
            } else {
                link.receive(frame);
                this.collisionCount[targetPort] = 0;
                transferController.reset();
                return true;
            }
        }
        return false;

    }

    /**
     * 提供给链路向交换机发送数据时调用的api
     *
     * @param frame
     */
    public void receive(EthernetFrame frame) {
        long sourceMAC = frame.getSourceMAC();
        this.links.forEach((port,link)->{
            if(link.containsMAC(sourceMAC)){
                this.switchTable.update(sourceMAC,port);
            }
        });
        this.buffer.add(frame);

    }

    /**
     * 提供给runtime平台调用构造基础网络的api
     *
     * @param link
     * @throws Exception 当接入链路达到上限时抛出
     */
    public void joinLink(PhisicalLink link) throws PortFullOccupiedException {
        if (this.links.size() > Switch.PORT_AMOUNT) {
            throw new PortFullOccupiedException();
        }
        int accessPort = this.random.nextInt(Switch.PORT_AMOUNT);
        if (!this.links.containsKey(accessPort)) {
            this.links.put(accessPort, link);
            this.activityContorllers.put(accessPort, new ActivityContorller());
            link.join(this);
        } else {
            this.joinLink(link);
        }
    }


    /**
     * 由Runtime轮询调用
     * 如果目标MAC在源端口对应的链路上，则不转发
     * 如果目标MAC所在链路对应的端口记录不存在，则向其他端口广播
     * 如果目标MAC在其他端口对应的链路，则向那个端口转发
     */
    @Schedule(period = 3000)
    public void transfer() {
        for (EthernetFrame frame : this.buffer) {
            this.buffer.addDeleteSignFor(frame);
            int targetPort = this.switchTable.getPortByMAC(frame.getTargetMAC());
            int sourcePort = this.switchTable.getPortByMAC(frame.getSourceMAC());
            if (targetPort != sourcePort) {
                if (targetPort != -1) {
                    if(!this.transferToPort(targetPort, frame)){
                        this.buffer.removeDeleteSignFor(frame);
                    }
                } else {
                    this.links.forEach((port, link) -> {
                        if (!link.containsMAC(frame.getSourceMAC())) {
                            if(!this.transferToPort(port, frame)){
                                this.buffer.removeDeleteSignFor(frame);
                            }
                        }
                    });
                }
            }
        }
        this.buffer.clean();
    }
}
