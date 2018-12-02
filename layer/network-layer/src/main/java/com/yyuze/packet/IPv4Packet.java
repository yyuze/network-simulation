package com.yyuze.packet;

/**
 * Author: yyuze
 * Time: 2018-11-29
 */
public class IPv4Packet {

    /**
     * 协议版本
     */
    private String version;

    /**
     * 首部长度
     */
    private String headerLength;

    /**
     * 服务类型
     */
    private String TOS;

    /**
     * 载荷长度
     */
    private String payloadLength;

    /**
     * 标志和片偏移
     */
    private String signAndOffset;

    /**
     * 寿命
     */
    private String TTL;

    /**
     * 上层协议
     */
    private String protocol;

    /**
     * 首部校验和
     */
    private String headerChecksum;

    /**
     * 源IP地址
     */
    private int sourceIP;

    /**
     * 目的IP地址
     */
    private int targetIP;

    /**
     * 选项
     */
    private String options;

    /**
     * 载荷
     */
    private String payload;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHeaderLength() {
        return headerLength;
    }

    public void setHeaderLength(String headerLength) {
        this.headerLength = headerLength;
    }

    public String getTOS() {
        return TOS;
    }

    public void setTOS(String TOS) {
        this.TOS = TOS;
    }

    public String getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(String payloadLength) {
        this.payloadLength = payloadLength;
    }

    public String getSignAndOffset() {
        return signAndOffset;
    }

    public void setSignAndOffset(String signAndOffset) {
        this.signAndOffset = signAndOffset;
    }

    public String getTTL() {
        return TTL;
    }

    public void setTTL(String TTL) {
        this.TTL = TTL;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHeaderChecksum() {
        return headerChecksum;
    }

    public void setHeaderChecksum(String headerChecksum) {
        this.headerChecksum = headerChecksum;
    }

    public int getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(int sourceIP) {
        this.sourceIP = sourceIP;
    }

    public int getTargetIP() {
        return targetIP;
    }

    public void setTargetIP(int targetIP) {
        this.targetIP = targetIP;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
      //todo
        return null;
    }
}
