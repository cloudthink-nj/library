package com.ibroadlink.library.aidl;

/**
 *  注意: 部分版本可能不支持AIDL中文注释
 *  通用返回状态码：
 */
import com.ibroadlink.library.aidl.IAidlCallback;
interface IAidlService {
    //回调
    void addCallback(IAidlCallback cb);
    void delCallback(IAidlCallback cb);
    //功能接口
    void requestAction(String action, String data);

    List<Bitmap> getBitmap(String action, String data);
}