package com.ibroadlink.screen;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.ibroadlink.library.aidlink.annotation.Callback;
import com.ibroadlink.library.aidlink.annotation.In;
import com.ibroadlink.library.aidlink.annotation.Inout;
import com.ibroadlink.library.aidlink.annotation.OneWay;
import com.ibroadlink.library.aidlink.annotation.Out;
import com.ibroadlink.library.aidlink.annotation.RemoteInterface;

/**
 * Created by codezjx on 2018/3/12.<br/>
 */
@RemoteInterface
public interface IRemoteService {

    int getPid();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                    double aDouble, String aString);

    void registerCallback(@Callback IRemoteCallback callback);

    void directionalParamMethod(@In int[] arr, @Out ParcelableObj obj, @Inout Rect rect);

    @OneWay
    void onewayMethod(String msg);

    Bitmap getBitmap();
}