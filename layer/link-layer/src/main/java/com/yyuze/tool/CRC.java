package com.yyuze.tool;

/**
 * Author: yyuze
 * Time: 2018-11-26
 */

/**
 * Cyclic Redundance Chck
 * 一个能生成和校验32位CRC值的工具
 * 在network adapter中验证帧数据的完整性时被使用
 */
public class CRC {

    private long GENERATOR_CRC32 = 0b100000100110000010001110110110111L;

    /**
     * 生成一个32位的CRC值
     * @param data 用于生成CRC值的数据
     * @return CRC
     */
    public long generateCRC(String data) {
        byte[] bytes = data.getBytes();
        long checksum = this.generateChecksum(bytes);
        long d = checksum << 32;
        return this.cyclicBinaryModular(d, this.GENERATOR_CRC32);
    }

    /**
     * 校验CRC值是否通过
     * @param data 需要校验的数据
     * @param crc 需要校验的CRC
     * @return 校验结果
     */
    public boolean check(String data, long crc) {
        byte[] bytes = data.getBytes();
        return this.cyclicBinaryModular((this.generateChecksum(bytes)<<32)^crc,this.GENERATOR_CRC32)==0;
    }

    /**
     * 生成32位校验和，位溢出采用回卷方式处理
     * @param bytes 用于生成校验和的数据
     * @return 校验和
     */
    private long generateChecksum(byte[] bytes) {
        int checksum = 0;
        for (int i = 0; i < bytes.length; i += 4) {
            int row = 0;
            for (int j = 0; j < 4; j++) {
                if (j + i >= bytes.length) {
                    break;
                }
                row |= bytes[i + j] << ((3 - j) * 8);
            }
            checksum += row;
            long checkOverflow = (long) checksum + (long) row;
            if ((checksum + row) != checkOverflow) {
                checksum += 1;
            }
        }
        return checksum;
    }

    /**
     * 使用模2算术求余数
     * @param devided 被除数
     * @param devidor 除数
     * @return 余数
     */
    private long cyclicBinaryModular(long devided, long devidor) {
        int devideLength = Long.toBinaryString(devidor).length();
        while (Long.toBinaryString(devided).length() >= devideLength) {
            long actualDevidor = devidor<<Long.toBinaryString(devided).length() - Long.toBinaryString(devidor).length();
            devided ^= actualDevidor;
        }
        return devided;
    }
}