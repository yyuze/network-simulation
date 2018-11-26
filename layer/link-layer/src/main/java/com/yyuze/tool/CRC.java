package com.yyuze.tool;

/**
 * Author: yyuze
 * Time: 2018-11-26
 */
public class CRC {

    private long GENERATOR_CRC32 = 0b100000100110000010001110110110111L;

    public long generateCRC(String data) {
        byte[] bytes = data.getBytes();
        long checksum = this.generateChecksum(bytes);
        long d = checksum << 32;
        return this.cyclicBinaryModular(d, this.GENERATOR_CRC32);
    }

    public boolean check(String data, long crc) {
        byte[] bytes = data.getBytes();
        return this.cyclicBinaryModular((this.generateChecksum(bytes)<<32)^crc,this.GENERATOR_CRC32)==0;
    }

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

    private long cyclicBinaryModular(long devided, long devidor) {
        int devideLength = Long.toBinaryString(devidor).length();
        while (Long.toBinaryString(devided).length() >= devideLength) {
            long actualDevidor = devidor<<Long.toBinaryString(devided).length() - Long.toBinaryString(devidor).length();
            devided ^= actualDevidor;
        }
        return devided;
    }
}