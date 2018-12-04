package com.yyuze.exception;

/**
 * Author: yyuze
 * Time: 2018-12-04
 */
public class PacketTypeException extends Exception{

    @Override
    public String getMessage() {
        return "wrong packet type";
    }
}
