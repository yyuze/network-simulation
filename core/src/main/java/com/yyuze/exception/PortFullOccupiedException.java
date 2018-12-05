package com.yyuze.exception;

/**
 * Author: yyuze
 * Time: 2018-12-05
 */
public class PortFullOccupiedException extends Exception{

    @Override
    public String getMessage() {
        return "交换机端口已满,无法接入";
    }

}
