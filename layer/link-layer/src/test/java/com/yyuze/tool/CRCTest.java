package com.yyuze.tool;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: yyuze
 * Time: 2018-11-26
 */
@DisplayName("CRC test start")
public class CRCTest {

    private static CRC crcTool;

    @BeforeAll
    @Test
    public static void initCRCTest() {
        System.out.println("CRC test start");
        CRCTest.crcTool = new CRC();
    }

    @DisplayName("CRC.generateChecksum()")
    @Test
    public void generateChecksumTest() {
        try {
            Method method = crcTool.getClass().getDeclaredMethod("generateChecksum", byte[].class);
            method.setAccessible(true);
            long checksum1 = (long) method.invoke(crcTool, new byte[]{1, 2, 3, 4, 5});
            long checksum2 = (long) method.invoke(crcTool, new byte[]{64, 0, 0, 0, 64, 0, 0, 0, 64, 0, 0, 0, 64, 0, 0, 0});
            //checksum1和checksum2的参考值为手工计算结果
            //checksum1是无bit回卷的校验和
            //checksum2是有bit回卷的校验和
            assert checksum1 == 0b00000110000000100000001100000100 : "checksum1 failed";
            System.out.println(method.getName() + "() sample1 passed");
            assert checksum2 == 1 : "checksum2 failed";
            System.out.println(method.getName() + "() sample2 passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("CRC.cyclicBinaryModular()")
    @Test
    public void cyclicBinaryModularTest() {
        try {
            Method method = crcTool.getClass().getDeclaredMethod("cyclicBinaryModular", long.class, long.class);
            method.setAccessible(true);
            long reminder = (long) method.invoke(crcTool, 0b101110000L, 0b1001L);
            //reminder的参考值为手工计算结果
            assert reminder == 0b011 : "failed";
            System.out.println(method.getName() + "() passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private long generateCRCTest(String data) {
        System.out.println("generateCRC() finished, provide a crc for check() test");
        return crcTool.generateCRC(data);
    }

    @DisplayName("CRC.check()")
    @Test
    public void checkTest() {
        String testData = "hello world";
        assert crcTool.check(testData, this.generateCRCTest(testData)) : "failed";
        System.out.println("check() passed");
        System.out.println("generateCRCTest() passed");
    }

    @AfterAll
    @Test
    public static void finish() {
        System.out.println("CRC test finished");
    }
}
