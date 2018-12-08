package com.yyuze.tool;

import com.yyuze.packet.BasePacket;

import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-12-03
 */
public class Buffer <T extends BasePacket> extends ArrayList<T>{

    private ArrayList<Integer> deleteSign;

    public Buffer(){
        super();
        this.deleteSign = new ArrayList<>();
    }

    public void addDeleteSignFor(T object){
        this.deleteSign.add(this.indexOf(object));
    }

    public void removeDeleteSignFor(T object){
        this.deleteSign.remove((Integer)this.indexOf(object));
    }

    public void clean(){
        ArrayList<T> b = new ArrayList<>();
        for(int i: this.deleteSign){
            b.add(this.get(i));
        }
        for(T obj : b){
            this.remove(b);
        }
        this.deleteSign = new ArrayList<>();
    }
}
