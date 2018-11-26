package com.yyuze.pkg;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class Frame {

    private Character[] preamble = new Character[]{0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101011};

    private long targetMAC;

    private long sourceMAC;

    private char type;

    private String payload;

    private Integer CRC;

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

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Integer getCRC() {
        return CRC;
    }

    public void setCRC(Integer CRC) {
        this.CRC = CRC;
    }
}
