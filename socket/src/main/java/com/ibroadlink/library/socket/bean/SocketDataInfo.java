package com.ibroadlink.library.socket.bean;

/**
 * Created by BroadLink_T on 2017/9/18.
 * description：消息队列数据
 */
public class SocketDataInfo {
    private short msgType;
    private long time;
    private byte[] payload;
    private String ipAddress;
    private int port;

    public SocketDataInfo() {
    }

    public SocketDataInfo(short msgType, long time, byte[] payload, String ipAddress, int port) {
        this.msgType = msgType;
        this.time = time;
        this.payload = payload;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public short getMsgType() {
        return msgType;
    }

    public void setMsgType(short msgType) {
        this.msgType = msgType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
