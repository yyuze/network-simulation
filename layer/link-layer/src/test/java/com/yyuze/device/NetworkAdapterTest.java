package com.yyuze.device;

import com.yyuze.pkg.Frame;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.CRC32;

/**
 * Author: yyuze
 * Time: 2018-11-25
 */
public class NetworkAdapterTest {

    @Test
    public void checkTest(){
        NetworkAdapter networkAdapter = new NetworkAdapter();
        Frame frame = new Frame();
        frame.setPayload("hello world");
        frame.setCRC(1822254807L);
        try {
            Method check = networkAdapter.getClass().getDeclaredMethod("check", Frame.class);
            check.setAccessible(true);
            if((Boolean) check.invoke(networkAdapter,frame)){
                System.out.println("test passed");
            }else {
                System.out.println("test failed");
            }

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void generateCRCTest(){
        NetworkAdapter networkAdapter = new NetworkAdapter();
        Frame frame = new Frame();
        frame.setPayload("hello world");
        try {
            Method generateCRC = networkAdapter.getClass().getDeclaredMethod("generateCRC", Frame.class);
            generateCRC.setAccessible(true);
            Long crc = (Long)generateCRC.invoke(networkAdapter,frame);
            //测试是否与权威CRC相同
            CRC32 crc32 = new CRC32();
            crc32.update(frame.getPayload().getBytes());
            System.out.println(crc);
            System.out.println(crc32.getValue());
            if(crc == crc32.getValue()){
                System.out.println("test passed");
            }else {
                System.out.println("test failed");
            }
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

    //(~(num1&num2))&(num1|num2)
    @Test
    public void xorTest(){
        System.out.println((~(1&1))&(1|1));
        System.out.println((~(1&0))&(1|0));
    }
}
