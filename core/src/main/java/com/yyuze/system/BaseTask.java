package com.yyuze.system;

import com.yyuze.tool.Console;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-13
 */
public abstract class BaseTask implements Runnable {

    protected final Lock deamonLock;

    protected final Console console;

    protected HashMap invokers;

    protected final Condition terminate;

    protected boolean run = true;

    public BaseTask(Lock deamonLock, Condition terminate, Console console, HashMap invokers) {
        this.console = console;
        this.deamonLock = deamonLock;
        this.terminate = terminate;
        this.invokers = invokers;
    }

    public void terminate() {
        this.run = false;
    }


    protected void signalToShutdown() {
        final Lock lock = this.deamonLock;
        lock.tryLock();
        try {
            this.terminate.signal();
            this.terminate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    protected void signalShuttedDown() {
        final Lock lock = this.deamonLock;
        lock.tryLock();
        try {
            terminate.signal();
        }finally {
            lock.unlock();
        }
    }


}
