package com.yyuze.table;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-30
 */


/**
 * 交换机的转发表
 */
public class SwitchTable {

    private ArrayList<SwitchItem> table;

    public SwitchTable(){
        this.table = new ArrayList<>();
    }

    private class SwitchItem {
        private long MAC;

        private int port;

        private long agingTime;

        public SwitchItem(long MAC, int port, long agingTime) {
            this.MAC = MAC;
            this.port = port;
            this.agingTime = agingTime;
        }

        public long getMAC() {
            return this.MAC;
        }

        public int getPort() {
            return this.port;
        }

        public long getAgingTime() {
            return this.agingTime;
        }
    }

    /**
     * 添加一条记录
     * @param MAC
     * @param port 端口
     * @param agingTime 老化时间
     */
    public void add(long MAC,int port,long agingTime){
        SwitchItem item = new SwitchItem(MAC,port,agingTime);
        this.table.add(item);
    }

    /**
     * 通过MAC地址查询端口
     * @param MAC
     * @return MAC地址对应的端口，如果没有，返回-1
     */
    public int getPortByMAC(long MAC){
        for (SwitchItem row : this.table){
            if(row.getMAC()==MAC){
                return row.getPort();
            }
        }
        return -1;
    }

}
