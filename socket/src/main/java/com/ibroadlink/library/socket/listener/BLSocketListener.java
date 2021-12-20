package com.ibroadlink.library.socket.listener;

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/11/18 14:11
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: xxx
 */
public interface BLSocketListener {
    void onMessageReceived(short msgType, byte[] payload, String serverIP, int serverPort);
}
