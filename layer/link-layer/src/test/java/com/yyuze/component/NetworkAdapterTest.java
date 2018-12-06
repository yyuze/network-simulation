package com.yyuze.component;

import com.yyuze.packet.EthernetFrame;
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
        networkAdapter = new NetworkAdapter(0xffffffffffffL);
    }

    @DisplayName("NetworkAdapter.check()")
    @Test
    public void checkTest() {
        EthernetFrame ethernetFrame = new EthernetFrame();
        ethernetFrame.setPayload("hello world");
        ethernetFrame.setCRC(488123962L);
        try {
            Method check = networkAdapter.getClass().getDeclaredMethod("check", EthernetFrame.class);
            check.setAccessible(true);
            assert (boolean) check.invoke(networkAdapter, ethernetFrame) : "failed";
            System.out.println("check() passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("NetworkAdapter.generateCRC()")
    @Test
    public void generateCRCTest() {
        EthernetFrame ethernetFrame1 = new EthernetFrame();
        ethernetFrame1.setPayload("hello world");
        EthernetFrame ethernetFrame2 = new EthernetFrame();
        ethernetFrame2.setPayload("hello worle");
        try {
            Method generateCRC = networkAdapter.getClass().getDeclaredMethod("generateCRC", EthernetFrame.class);
            generateCRC.setAccessible(true);
            long crc1 = (long) generateCRC.invoke(networkAdapter, ethernetFrame1);
            long crc2 = (long) generateCRC.invoke(networkAdapter, ethernetFrame2);
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
