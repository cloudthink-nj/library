package com.ibroadlink.library.socket.utils;


import com.ibroadlink.library.socket.bean.MessageHeader;

public class BLProtoUtils {
    //开始发现广播
    public static final short DISCOVERY_REQ = 0x0820;
    //发现广播
    public static final short DISCOVERY_ACK = 0x0821;
    // IHG -> 智慧屏 心跳请求
    public static final short HEART_REQUEST = 0x0823;
    //智慧屏 -> IHG 心跳响应
    public static final short HEART_RESPONSE = 0x0824;
    //智慧屏 -> IHG 请求/上报
    public static final short DEV_GATEWAY_REQ = 0x0825;
    //IHG -> 智慧屏 请求/下发
    public static final short GATEWAY_DEV_REQ = 0x0826;
    //双向ACK消息
    public static final short REQUEST_ACK = 0x0827;

    public static MessageHeader bytes2Head(byte[] bytes) {
        MessageHeader head = new MessageHeader();
        byte[] magic = new byte[4];
        byte[] checksum = new byte[2];
        byte[] cmd_type = new byte[2];
        byte[] data_len = new byte[2];
        byte[] version = new byte[2];
        System.arraycopy(bytes, 0, magic, 0, 4);
        System.arraycopy(bytes, 4, checksum, 0, 2);
        System.arraycopy(bytes, 6, cmd_type, 0, 2);
        System.arraycopy(bytes, 8, data_len, 0, 2);
        System.arraycopy(bytes, 10, version, 0, 2);
        head.magic_code = bytesToInt(magic);
        head.checksum = bytesToShort(checksum);
        head.msg_type = bytesToShort(cmd_type);
        head.data_len = bytesToShort(data_len);
        head.reserved = bytesToShort(version);
        return head;
    }

    public static byte[] packageSendData(short msgType, byte[] bPayload) {
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.data_len = (short) bPayload.length;
        messageHeader.msg_type = msgType;
        messageHeader.checksum = makeCheckSum(messageHeader, bPayload);
        byte[] bHead = BLProtoUtils.head2Bytes(messageHeader);
        byte[] dataPram = new byte[bPayload.length + bHead.length];
        System.arraycopy(bHead, 0, dataPram, 0, bHead.length);
        System.arraycopy(bPayload, 0, dataPram, bHead.length, bPayload.length);
        return dataPram;
    }

    private static int makeCheckSum(MessageHeader header, byte[] data) {
        int sum = (header.magic_code + header.msg_type + header.data_len + header.reserved) & 0xFFFF;
        for (byte byteMsg : data) {
            long mNum = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            sum += mNum;
        }
        return sum;
    }

    private static byte[] head2Bytes(MessageHeader head) {
        byte[] tHead = new byte[12];

        byte[] magic = uintToBytes(head.magic_code);
        byte[] checksum = ushortToBytes(head.checksum);
        byte[] msg_type = ushortToBytes(head.msg_type);
        byte[] data_len = ushortToBytes(head.data_len);
        byte[] reserve = ushortToBytes(head.reserved);

        System.arraycopy(magic, 0, tHead, 0, 4);
        System.arraycopy(checksum, 0, tHead, 4, 2);
        System.arraycopy(msg_type, 0, tHead, 6, 2);
        System.arraycopy(data_len, 0, tHead, 8, 2);
        System.arraycopy(reserve, 0, tHead, 10, 2);
        return tHead;
    }

    private static int bytesToInt(byte[] b) {
        return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;
    }

    public static byte[] uintToBytes(long n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    private static short bytesToShort(byte[] b) {
        return (short) (b[1] & 0xff | (b[0] & 0xff) << 8);
    }

    private static byte[] ushortToBytes(int n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }
}

