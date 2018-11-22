package com.yyuze.device;


import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-11-24
 */
public class NetworkAdapterTest {

    @Test
    private void generateCRCTest(){
        NetworkAdapter networkAdapter = new NetworkAdapter();
        try {
            Method generateCRC = networkAdapter.getClass().getDeclaredMethod("generateCRC", String.class);
            generateCRC.invoke(networkAdapter,"");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    private void test(){
        String text = "abc";
        byte[] bytes = text.getBytes();
        System.out.print(bytes[0]+"123");
    }

}
