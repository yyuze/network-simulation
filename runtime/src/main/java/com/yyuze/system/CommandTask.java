package com.yyuze.system;

import com.yyuze.enums.CommandEnum;
import com.yyuze.tool.Console;
import com.yyuze.tool.Invoker;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Author: yyuze
 * Time: 2018-12-06
 */
public class CommandTask extends BaseTask {


    private final String ILLEGAL_COMMAND = "illegal command,input --help to get command list.";

    private final String WELCOME_INFO = "input a command: ";

    private final String DETAIL_INPUT = "input the detail of command";

    private final String ERROR_INPUT = "parameters error";

    private final String SUCCESS = "command executed";

    public CommandTask(Lock deamonLock, Condition terminate, HashMap<String, Invoker> invokers, Console console) {
        super(deamonLock, terminate, console, invokers);
    }

    @Override
    public void run() {
        while (this.run) {
            this.console.write(this.WELCOME_INFO);
            //System.out.println(this.WELCOME_INFO);
            String command = this.console.read();
            /**
             * 关闭程序
             */
            if (command.equals(CommandEnum.shut_down.toString())) {
                this.signalToShutdown();
                continue;
            }
            if (!this.invokers.containsKey(command)) {
                this.console.write(this.ILLEGAL_COMMAND);
            } else {
                this.console.write(this.DETAIL_INPUT);
                String paras = "";
                String line = this.console.read();
                while (!line.equals("")) {
                    paras += line + "\n";
                    line = this.console.read();
                }
                /**
                 * 该线程堵塞至一条命令执行完
                 */
                if (!((Invoker) this.invokers.get(command)).invoke(paras)) {
                    this.console.write(this.ERROR_INPUT);
                } else {
                    this.console.write(this.SUCCESS);
                }
            }
        }
        this.console.write("Command System shutted down");
        this.signalShuttedDown();
    }
}
