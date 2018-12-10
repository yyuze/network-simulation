package com.yyuze.system;


import com.yyuze.tool.Invoker;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-06
 */
public class ScheduleTask implements Runnable{

    private final Lock lock;

    private final HashMap<Long, Invoker> scheduleInvokers;

    public ScheduleTask(Lock lock, HashMap<Long, Invoker> scheduleInvokers) {
        this.lock = lock;
        this.scheduleInvokers = scheduleInvokers;
    }


    @Override
    public void run() {

    }
}
