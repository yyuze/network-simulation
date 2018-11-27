package com.yyuze.device;

import com.yyuze.pkg.Frame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-11-25
 */
public class NetworkAdapterTest {

    private static NetworkAdapter networkAdapter;

    @BeforeAll
    @Test
    public static void initNetworkAdapterTest() {
        System.out.println("NetworkAdapter test start");
        networkAdapter = new NetworkAdapter();
    }

    @DisplayName("NetworkAdapter.check()")
    @Test
    public void checkTest() {
        Frame frame = new Frame();
        frame.setPayload("hello world");
        frame.setCRC(488123962L);
        try {
            Method check = networkAdapter.getClass().getDeclaredMethod("check", Frame.class);
            check.setAccessible(true);
            assert (boolean) check.invoke(networkAdapter, frame) : "failed";
            System.out.println("check() passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("NetworkAdapter.generateCRC()")
    @Test
    public void generateCRCTest() {
        NetworkAdapter networkAdapter = new NetworkAdapter();
        Frame frame1 = new Frame();
        frame1.setPayload("hello world");
        Frame frame2 = new Frame();
        frame2.setPayload("hello worle");
        try {
            Method generateCRC = networkAdapter.getClass().getDeclaredMethod("generateCRC", Frame.class);
            generateCRC.setAccessible(true);
            long crc1 = (long) generateCRC.invoke(networkAdapter, frame1);
            long crc2 = (long) generateCRC.invoke(networkAdapter, frame2);
            assert crc1 != crc2 : "failed";
            System.out.println("generateCRCTest() passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    @Test
    public static void finished() {
        System.out.println("NetworkAdapter test finished\n");
    }


}
