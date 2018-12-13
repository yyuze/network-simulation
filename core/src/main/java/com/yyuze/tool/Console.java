package com.yyuze.tool;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: yyuze
 * Time: 2018-12-13
 */
public final class Console {

    private final ReentrantLock lock = new ReentrantLock();


    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    public void write(String str) {
        final Lock lock = this.lock;
        lock.lock();
        try {
            this.writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public String read() {
        final Lock lock = this.lock;
        try {
            return this.reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return null;
    }
}
