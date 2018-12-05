package com.yyuze.invoker.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */

//todo
public class CommandInvoker {

    private Object invoker;

    private Method method;

    public CommandInvoker(Object invoker, Method method) {
        this.invoker = invoker;
        this.method = method;
    }

    public void invoke(){
        try {
            method.invoke(invoker);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
