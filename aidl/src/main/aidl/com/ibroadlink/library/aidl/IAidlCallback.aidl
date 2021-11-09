package com.ibroadlink.library.aidl;

/**
 *  注意: 部分版本可能不支持AIDL中文注释
 *  通用返回状态码：
 */
interface IAidlCallback {
    //回调
    void onCallback(String action, String data);
}