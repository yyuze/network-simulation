package com.yyuze.tool;

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

    @DisplayName("CRC.generateChecksum() test start")
    @Test
    public void generateChecksumTest() {
        CRC crcTool = new CRC();
        try {
            Method method = crcTool.getClass().getDeclaredMethod("generateChecksum", byte[].class);
            method.setAccessible(true);
            int checksum1 = (int) method.invoke(crcTool, new byte[]{1, 2, 3, 4, 5});
            int checksum2 = (int) method.invoke(crcTool, new byte[]{64, 0, 0, 0, 64, 0, 0, 0, 64, 0, 0, 0, 64, 0, 0, 0});
            //checksum1和checksum2的参考值为手工计算结果
            //checksum1是无bit回卷的校验和
            //checksum2是有bit回卷的校验和
            assert checksum1 == 0b00000110000000100000001100000100 : "checksum1 failed";
            System.out.println(method.getName() + " checksum1 pass");
            assert checksum2 == 1 : "checksum2 failed";
            System.out.println(method.getName() + " checksum2 pass");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("CRC.cyclicBinaryModular() test start")
    @Test
    public void cyclicBinaryModularTest() {
        CRC crcTool = new CRC();
        try {
            Method method = crcTool.getClass().getDeclaredMethod("cyclicBinaryModular", long.class, long.class);
            method.setAccessible(true);
            int reminder = (int) method.invoke(crcTool, 0b101110000L, 0b1001L);
            //reminder的参考值为手工计算结果
            assert reminder == 0b011 : "failed";
            System.out.println(method.getName() + " passed");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("CRC.generateCRC() test start")
    @Test
    public int generateCRCTest() {
        CRC crcTool = new CRC();
        int crc = crcTool.generateCRC("hello world");
        System.out.println(crc);
        return crc;
    }

    @DisplayName("CRC.check() test start")
    @Test
    public void checkTest() {
        CRC crcTool = new CRC();
        assert crcTool.check("hello world",this.generateCRCTest()):"failed";
    }
}
