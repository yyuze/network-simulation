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
        frame.setCRC(1822254807);
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
            long crc = (long)generateCRC.invoke(networkAdapter,frame);
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



}
