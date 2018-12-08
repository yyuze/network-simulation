package com.yyuze.component;

import com.yyuze.exception.PacketTypeException;
import com.yyuze.packet.EthernetFrame;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Author: yyuze
 * Time: 2018-11-25
 */
public class NetworkAdapterTest {

    private static NetworkAdapter networkAdapter;

    private static long deviceMAC = 0x12345678L;

    @BeforeAll
    @Test
    public static void initNetworkAdapterTest() {
        System.out.println("NetworkAdapter test start");
        networkAdapter = new NetworkAdapter(deviceMAC);
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
            assert (boolean) check.invoke(networkAdapter, ethernetFrame) : "check() failed";
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
            assert crc1 != crc2 : "generateCRCTest() failed";
            System.out.println("generateCRCTest() passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("NetworkAdapter.joinLinkTest()")
    @Test
    public void joinLinkTest() {
        long linkSerial = 0x00000000L;
        PhisicalLink link = new PhisicalLink(linkSerial);
        networkAdapter.joinLink(link);
        boolean passFlag1 = networkAdapter.getLinkSerial() == linkSerial;
        boolean passFlag2 = link.containsMAC(deviceMAC);
        assert passFlag1 && passFlag2 : "joinLinkTest() faild";
        System.out.println("joinLinkTest() passed");
    }

    @Test
    public void receiveFromUpperTest(){

        long targetMAC = 0x12345677L;
        EthernetFrame frame = new EthernetFrame();
        frame.setTargetMAC(targetMAC);
        String payload = "hello wolrd";
        frame.setPayload(payload);
        boolean isPassed = true;
        try {
            networkAdapter.receiveFromUpper(frame);
            Field field = networkAdapter.getClass().getDeclaredField("toLowerBuffer");
            field.setAccessible(true);
            ArrayList<EthernetFrame> buffer = (ArrayList<EthernetFrame>) field.get(networkAdapter);
            isPassed &= buffer.get(0).getPayload()==payload&&buffer.get(0).getTargetMAC()==targetMAC;
        } catch (PacketTypeException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            isPassed = false;
        }
        assert isPassed : "receiveFromUpper() failed";
        System.out.println("receiveFromUpper() passed");

    }

    @Test
    public void sendToLowerTest() throws PacketTypeException, NoSuchFieldException, IllegalAccessException {

        long targetMAC = 0x12345677L;
        EthernetFrame frame1 = new EthernetFrame();
        frame1.setTargetMAC(targetMAC);
        frame1.setPayload("0");
        networkAdapter.receiveFromUpper(frame1);
        EthernetFrame frame2 = new EthernetFrame();
        frame2.setTargetMAC(targetMAC);
        frame2.setPayload("1");
        networkAdapter.receiveFromUpper(frame2);
        EthernetFrame frame3 = new EthernetFrame();
        frame3.setTargetMAC(targetMAC);
        frame3.setPayload("2");
        networkAdapter.receiveFromUpper(frame3);

        long linkSerial = 0x00000000L;
        PhisicalLink link = new PhisicalLink(linkSerial);
        NetworkAdapter another = new NetworkAdapter(targetMAC);
        another.joinLink(link);
        networkAdapter.joinLink(link);
        networkAdapter.sendToLower();
        Field field = another.getClass().getDeclaredField("toUpperBuffer");
        field.setAccessible(true);
        ArrayList<EthernetFrame> buffer = (ArrayList<EthernetFrame>) field.get(another);
        boolean flag = true;
        for(int i = 0 ; i < 3 ; i++){
            flag &= buffer.get(i).getPayload().equals(""+i);
        }
        assert flag : "sendToLowerTest() failed";
    }

    @AfterAll
    @Test
    public static void finished() {
        System.out.println("NetworkAdapter test finished\n");
    }


}
