package com.yyuze.tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */

public class Invoker {

    private Object invokeObj;

    private Method method;


    public Invoker(Object invokeObj, Method method) {
        this.invokeObj = invokeObj;
        this.method = method;
        this.method.setAccessible(true);
    }

    public boolean invoke(Object... args) {
        try {
            this.method.invoke(this.invokeObj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getMethodName(){
        return this.method.getName();
    }

}
