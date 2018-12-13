package com.yyuze.system;

import com.yyuze.tool.Console;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-13
 */
public abstract class BaseTask implements Runnable {

    protected final Lock lock;

    protected final Console console;

    protected HashMap invokers;

    protected boolean run = true;

    public BaseTask( Lock lock,Console console,HashMap invokers){
        this.console = console;
        this.lock = lock;
        this.invokers = invokers;
    }

    public void terminate(){
        final Lock lock = this.lock;
        lock.tryLock();
        try{
            this.run = false;
        }finally {
            lock.unlock();
        }
    }

}
