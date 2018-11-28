package com.yyuze.pkg;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class EthernetFrame {

    /**
     * 前同步码
     */
    private Character[] preamble = new Character[]{0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101011};

    /**
     * 目的MAC地址
     */
    private long targetMAC;

    /**
     * 源MAC地址
     */
    private long sourceMAC;

    /**
     * 类型
     */
    private int type;

    /**
     * 数据载荷
     */
    private String payload;

    /**
     * 确保数据完整性的CRC
     */
    private long CRC;

    public Character[] getPreamble() {
        return preamble;
    }

    public void setPreamble(Character[] preamble) {
        this.preamble = preamble;
    }

    public long getTargetMAC() {
        return targetMAC;
    }

    public void setTargetMAC(long targetMAC) {
        this.targetMAC = targetMAC;
    }

    public long getSourceMAC() {
        return sourceMAC;
    }

    public void setSourceMAC(long sourceMAC) {
        this.sourceMAC = sourceMAC;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getCRC() {
        return CRC;
    }

    public void setCRC(long CRC) {
        this.CRC = CRC;
    }
}
