package com.yyuze.device;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-11-25
 */
public class NetworkAdapterTest {

    @Test
    public void generateCRCTest(){
        NetworkAdapter networkAdapter = new NetworkAdapter();
        try {
            Method generateCRC = networkAdapter.getClass().getDeclaredMethod("generateCRC", String.class);
            Long checkSum = (Long)generateCRC.invoke(networkAdapter,"");
            System.out.print(checkSum);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void bitCalculeteTest(){
        byte[] buf = {'a','b',1,2,3};
        long c = 0;
        for(byte b:buf){
            String number = Long.toBinaryString(b);
            if(number.length()<8){
                for(int i=number.length();i<8;i++){
                    number = "0"+number;
                }
            }
            System.out.print(number);
            c|=b;
            c<<=8;
        }
        c>>=8;
        System.out.println("\n0"+Long.toBinaryString(c));
    }
}
