package com.ibroadlink.screen;


import com.ibroadlink.library.aidlink.annotation.RemoteInterface;

/**
 * Created by codezjx on 2018/3/13.<br/>
 */
@RemoteInterface
public interface IRemoteCallback {

    void onStart();

    void onValueChange(int value);
}