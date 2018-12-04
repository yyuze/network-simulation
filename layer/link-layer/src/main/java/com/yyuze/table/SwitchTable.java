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

    //TTL = 600s = 10min
    public static long TTL = 10 * 60;

    private ArrayList<SwitchItem> table;

    public SwitchTable() {
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

        public void setMAC(long MAC) {
            this.MAC = MAC;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setAgingTime(long agingTime) {
            this.agingTime = agingTime;
        }
    }

    /**
     * 当一个帧到达的时候更新MAC地址的老化时间，如果超时则删除
     */
    private void removeTimeoutRecord() {
        long currentTime = System.currentTimeMillis() / 1000;
        ArrayList<Integer> timeoutItemIndex = new ArrayList<>();
        for (SwitchItem item : this.table) {
            boolean isTimeout = currentTime - item.getAgingTime() > SwitchTable.TTL;
            if (isTimeout) {
                timeoutItemIndex.add(this.table.indexOf(item));
            }
        }
        for (int i : timeoutItemIndex) {
            this.table.remove(i);
        }
    }

    /**
     * 添加一条记录
     *
     * @param MAC
     * @param port 端口
     */
    public void update(long MAC, int port) {
        long agingTime = System.currentTimeMillis() / 1000 + SwitchTable.TTL;//10min
        SwitchItem item = new SwitchItem(MAC, port, agingTime);
        boolean isNew = true;
        for (SwitchItem row : this.table) {
            if (row.getMAC() == MAC) {
                if (row.getPort() == port) {
                    row.setAgingTime(agingTime);
                } else {
                    row.setPort(port);
                }
                isNew = false;
            }
        }
        if (isNew) {
            this.table.add(item);
        }
        this.removeTimeoutRecord();
    }

    /**
     * 通过MAC地址查询端口
     *
     * @param MAC
     * @return MAC地址对应的端口，如果没有，返回-1
     */
    public int getPortByMAC(long MAC) {
        for (SwitchItem row : this.table) {
            if (row.getMAC() == MAC) {
                return row.getPort();
            }
        }
        return -1;
    }
}
