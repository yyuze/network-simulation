package com.yyuze.pkg;

/**
 * Author: yyuze
 * Time: 2018-11-19
 */
public class Frame {

    private Character[] preamble = new Character[]{0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101010, 0b1010101011};

    private Long targetMAC;

    private Long sourceMAC;

    private char type;

    private String payload;

    private Long CRC;

    public Character[] getPreamble() {
        return preamble;
    }

    public void setPreamble(Character[] preamble) {
        this.preamble = preamble;
    }

    public Long getTargetMAC() {
        return targetMAC;
    }

    public void setTargetMAC(Long targetMAC) {
        this.targetMAC = targetMAC;
    }

    public Long getSourceMAC() {
        return sourceMAC;
    }

    public void setSourceMAC(Long sourceMAC) {
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

    public Long getCRC() {
        return CRC;
    }

    public void setCRC(Long CRC) {
        this.CRC = CRC;
    }
}
