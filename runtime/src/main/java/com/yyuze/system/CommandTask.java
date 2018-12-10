package com.yyuze.system;

import com.yyuze.tool.Invoker;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-06
 */
public class CommandTask implements Runnable {

    private final Lock lock;

    private Condition condition;

    private final HashMap<String, Invoker> commandInvokers;

    private final Console console = new Console();

    private final String ILLEGAL_COMMAND = "illegal command,input --help to get command list.";

    private final String WELCOME_INFO = "input a command: ";

    private final String DETAIL_INPUT = "input the detail of command";

    private final String ERROR_INPUT = "parameters error";

    private final String SUCCESS = "command executed";

    public CommandTask(Lock lock, HashMap<String, Invoker> invokers) {
        this.lock = lock;
        this.condition = lock.newCondition();
        this.commandInvokers = invokers;
    }

    private final class Console {

        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        public void write(String str) {
            try {
                this.writer.write(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String read() {
            try {
                return this.reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void run() {
        final Lock lock = this.lock;
        lock.lock();
        try {
            while (true) {
                this.console.write(this.WELCOME_INFO);
                String command = this.console.read();
                if (!this.commandInvokers.containsKey(command)) {
                    this.console.write(this.ILLEGAL_COMMAND);
                    continue;
                } else {
                    this.console.write(this.DETAIL_INPUT);
                    String paras = "";
                    String line = this.console.read();
                    while (!line.equals("")) {
                        paras += line + "\n";
                    }
                    /**
                     * 该线程堵塞至一条命令执行完
                     */
                    if (!this.commandInvokers.get(command).invoke(paras)) {
                        this.console.write(this.ERROR_INPUT);
                    } else {
                        this.console.write(this.SUCCESS);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
