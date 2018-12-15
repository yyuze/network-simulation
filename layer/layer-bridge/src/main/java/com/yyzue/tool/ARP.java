package com.yyzue.tool;

/**
 * Author: yyuze
 * Time: 2018-11-21
 */

import com.yyuze.table.SwitchTable;

import java.util.ArrayList;

/**
 * Address Resolution Protocol
 */
public class ARP {
    
    ArrayList<ARPItem> table;
    
    public ARP(){
        this.table = new ArrayList<>();
    }

    private class ARPItem {
        
        long ipAddr;
        
        long MAC;
        
        long agingTime;

        public ARPItem(long ipAddr, long MAC, long agingTime) {
            this.ipAddr = ipAddr;
            this.MAC = MAC;
            this.agingTime = agingTime;
        }

        public long getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(long ipAddr) {
            this.ipAddr = ipAddr;
        }

        public long getMAC() {
            return MAC;
        }

        public void setMAC(long MAC) {
            this.MAC = MAC;
        }

        public long getAgingTime() {
            return agingTime;
        }

        public void setAgingTime(long agingTime) {
            this.agingTime = agingTime;
        }
    }
    
    public long getMACByIP(long ipAddr){
        for(ARPItem row : this.table){
            if(row.getIpAddr()==ipAddr){
                return row.MAC;
            }
        }
        return -1;
    }

    public void update(long ipAddr,long MAC){
        long agingTime = System.currentTimeMillis() / 1000 + SwitchTable.TTL;//10min
        ARPItem ARPItem = new ARPItem(ipAddr,MAC,agingTime);
        boolean isNew = true;
        for (ARPItem row : this.table) {
            if (row.getIpAddr() == ipAddr) {
                if (row.getMAC() == MAC) {
                    row.setAgingTime(agingTime);
                } else {
                    row.setMAC(MAC);
                }
                isNew = false;
            }
        }
        if (isNew) {
            this.table.add(ARPItem);
        }
        this.removeTimeoutRecord();
    }

    /**
     * 当一个帧到达的时候更新MAC地址的老化时间，如果超时则删除
     */
    private void removeTimeoutRecord() {
        long currentTime = System.currentTimeMillis() / 1000;
        ArrayList<Integer> timeoutItemIndex = new ArrayList<>();
        for (ARPItem ARPItem : this.table) {
            boolean isTimeout = currentTime - ARPItem.getAgingTime() > SwitchTable.TTL;
            if (isTimeout) {
                timeoutItemIndex.add(this.table.indexOf(ARPItem));
            }
        }
        for (int i : timeoutItemIndex) {
            this.table.remove(i);
        }
    }
}
