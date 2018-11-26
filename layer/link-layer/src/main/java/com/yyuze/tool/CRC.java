package com.yyuze.tool;

/**
 * Author: yyuze
 * Time: 2018-11-26
 */
public class CRC {

    private long GENERATOR_CRC32 = 0b100000100110000010001110110110111L;

    public int generateCRC(String data) {
        byte[] bytes = data.getBytes();
        int checksum = this.generateChecksum(bytes);
        long d = ((long) checksum) << 32;
        return this.cyclicBinaryModular(d, this.GENERATOR_CRC32);
    }

    public boolean check(String data, int crc) {
        byte[] bytes = data.getBytes();
        System.out.println((((long) this.generateChecksum(bytes) << 32) ^ crc) % this.GENERATOR_CRC32 );
        return (((long) this.generateChecksum(bytes) << 32) ^ crc) % this.GENERATOR_CRC32 == 0;
    }

    private int generateChecksum(byte[] bytes) {
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

    private int cyclicBinaryModular(long devided, long devidor) {
        int devideLength = Long.toBinaryString(devidor).length();
        while (Long.toBinaryString(devided).length() >= devideLength) {
            long actualDevidor = devidor<<Long.toBinaryString(devided).length() - Long.toBinaryString(devidor).length();
            devided ^= actualDevidor;
        }
        return (int) devided;
    }
}