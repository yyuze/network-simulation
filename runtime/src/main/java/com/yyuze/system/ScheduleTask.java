package com.yyuze.system;


import com.yyuze.tool.Console;
import com.yyuze.tool.Invoker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-06
 */
public class ScheduleTask extends BaseTask {

    public ScheduleTask(Lock lock, HashMap<Long, ArrayList<Invoker>> scheduleInvokers, Console console) {
        super(lock,console,scheduleInvokers);
    }

    @Override
    public void run() {
        final Lock lock = this.lock;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Long> schedules = new ArrayList<>();
            schedules.addAll(this.invokers.keySet());
            while (this.run) {
                int index = 0;
                try {
                    Long schedule = schedules.get(index);
                    Thread.sleep(schedule);
                    ArrayList<Invoker> invokers = (ArrayList<Invoker>)this.invokers.get(schedule);
                    for(Invoker invoker:invokers){
                        if(!invoker.invoke()){
                            console.write(invoker.getMethodName()+"invoke failed");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(++index > schedules.size()-1){
                    index = 0;
                }
            }
        } finally {
            lock.unlock();
        }
        this.console.write("schedule system is shutting down");
    }
}
