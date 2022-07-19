package com.ibroadlink.library.socket;

import android.text.TextUtils;
import android.util.Log;

import com.ibroadlink.library.socket.bean.MessageHeader;
import com.ibroadlink.library.socket.bean.SocketDataInfo;
import com.ibroadlink.library.socket.listener.BLSocketListener;
import com.ibroadlink.library.socket.utils.BLProtoUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class BLUdpSocket {

    private DatagramSocket mUdpClient;
    private DatagramPacket mReceivePacket;
    private Thread mDeliverThread;
    private Thread mReceiveThread;

    private byte[] mReceiveBuffers = new byte[BUFFER_LENGTH];
    private static final int BUFFER_LENGTH = 2048;
    private int mBufferDataLength = 0;
    //消息头数据长度
    private final int PACKAGE_HEAD_LENGTH = 12;
    private final byte[] mCacheMagicCode = new byte[4];

    private final LinkedBlockingQueue<SocketDataInfo> mDeliverQueue = new LinkedBlockingQueue<>(1024);

    private BLSocketListener mListener;

    public void start(int port, BLSocketListener listener) {
        // 本地端口号10245
        mListener = listener;
        mReceivePacket = new DatagramPacket(mReceiveBuffers, BUFFER_LENGTH);
        try {
            mUdpClient = new DatagramSocket(new InetSocketAddress(port));
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e("BLSocket", "new DatagramSocket FAILED: ", e);
        }

        //开启接收数据的线程
        mReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("BLSocket", "ReceiveThread is running...");
                receiveMessage();
            }
        });
        mReceiveThread.start();

        //开启发送数据的线程
        mDeliverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("BLSocket", "DeliverThread is running...");
                deliverMessage();
            }
        });
        mDeliverThread.start();
    }

    public void deliverMessage(short msgType, byte[] payload, String serverIP, int serverPort) {
        try {
            SocketDataInfo info = new SocketDataInfo();
            info.setMsgType(msgType);
            info.setPayload(payload);
            info.setIpAddress(serverIP);
            info.setPort(serverPort);
            info.setTime(System.currentTimeMillis());
            mDeliverQueue.put(info);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("BLSocket", "deliverMessage FAILED: ", e);
        }
    }

    public void destroy() {
        mListener = null;
        if (mReceiveThread != null) {
            mReceiveThread.interrupt();
        }
        if (mDeliverThread != null) {
            mDeliverThread.interrupt();
        }
        if (mUdpClient != null) {
            mUdpClient.close();
            mUdpClient = null;
        }
    }

    private void deliverMessage() {
        while (!mDeliverThread.isInterrupted()) {
            try {
                SocketDataInfo info = mDeliverQueue.take();
                if (TextUtils.isEmpty(info.getIpAddress())) {
                    Log.e("BLSocket", "UDP SOCKET FAILED: IP is null");
                    continue;
                }
                InetAddress address = InetAddress.getByName(info.getIpAddress());
                byte[] sendData = BLProtoUtils.packageSendData(info.getMsgType(), info.getPayload());
                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, address, info.getPort());
                if (mUdpClient != null) {
                    mUdpClient.send(packet);
                } else {
                    Log.e("BLSocket", "UDP SOCKET FAILED: mUdpClient == null ");
                }
            } catch (IOException | InterruptedException e) {
                Log.e("BLSocket", "UDP SOCKET FAILED: ", e);
            }
        }
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (!mReceiveThread.isInterrupted()) {
            try {
                if (mUdpClient != null) {
                    mUdpClient.receive(mReceivePacket);
                }
            } catch (IOException e) {
                Log.e("BLSocket", "UDP数据包接收失败！线程停止: " + e.getMessage());
                continue;
            }

            if (mReceivePacket == null || mReceivePacket.getLength() == 0) {
                Log.e("BLSocket", "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }
            if ( mReceivePacket.getAddress() == null) {
                Log.e("BLSocket", "UDP数据包InetAddress为空");
                continue;
            }
            String senderIP = mReceivePacket.getAddress().getHostAddress();
            if (senderIP != null) {
                for (int i = 0; i < mReceivePacket.getLength(); i++) {
                    int length = BUFFER_LENGTH - mBufferDataLength;
                    length = Math.min(length, mReceivePacket.getLength() - i);
                    System.arraycopy(mReceivePacket.getData(), i, mReceiveBuffers, mBufferDataLength, length);
                    mBufferDataLength = mBufferDataLength + length;
                    i = i + length - 1;
                    handleData(senderIP, mReceivePacket.getPort(), mReceiveBuffers);
                }
            }

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (mReceivePacket != null) {
                mReceivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    private void handleData(String address, int port, byte[] srcDataBytes) {
        if (mBufferDataLength > PACKAGE_HEAD_LENGTH) {
            int spiltIndex = 0;
            for (int index = 0; index < mBufferDataLength; index++) {
                //已经遍历到最后一个数据
                if (spiltIndex == 0 && index == srcDataBytes.length - 1) {
                    spiltIndex = srcDataBytes.length - 1;
                }

                if (isCorrectMagicCode(srcDataBytes, index)) {
                    spiltIndex = index;
                    //取出数据长度
                    byte[] headData = new byte[PACKAGE_HEAD_LENGTH];
                    System.arraycopy(srcDataBytes, index, headData, 0, PACKAGE_HEAD_LENGTH);
                    MessageHeader header = BLProtoUtils.bytes2Head(headData);
                    int dataLength = header.data_len;
                    //如果BUFFER中剩余的数据大于要接受的数据长度则将数据取出
                    if (mBufferDataLength - index - PACKAGE_HEAD_LENGTH >= dataLength) {
                        //取出数据
                        byte[] payload = getData(srcDataBytes, index, dataLength);
                        //放入消息队列
                        if (mListener != null) {
                            mListener.onMessageReceived(header.msg_type, payload, address, port);
                        }
                        index = index + PACKAGE_HEAD_LENGTH + dataLength;
                        index = index - 1;
                        spiltIndex = index;
                    }
                }
            }

            if (spiltIndex != 0) {
                int length = mBufferDataLength - spiltIndex;
                if (length >= 0) {
                    mBufferDataLength = length;
                    byte[] bufferData = new byte[BUFFER_LENGTH];
                    System.arraycopy(srcDataBytes, spiltIndex, bufferData, 0, mBufferDataLength);
                    mReceiveBuffers = bufferData;
                }
            }
        }
    }

    //判断是否正确的magic code
    private synchronized boolean isCorrectMagicCode(byte[] srcDataBytes, int index) {
        //数据大于Urt消息头 就去获取
        if (mBufferDataLength > index + PACKAGE_HEAD_LENGTH) {
            //拷贝4个字节，判断是否是正确magic code
            System.arraycopy(srcDataBytes, index, mCacheMagicCode, 0, 4);
            return (mCacheMagicCode[0] == 90) && (mCacheMagicCode[1] == 90)
                    && (mCacheMagicCode[2] == -91) && (mCacheMagicCode[3] == -91);
        }
        return false;
    }

    private byte[] getData(byte[] srcData, int index, int dataLength) {
        byte[] data = new byte[dataLength];
        System.arraycopy(srcData, index + PACKAGE_HEAD_LENGTH, data, 0, dataLength);
        return data;
    }
}