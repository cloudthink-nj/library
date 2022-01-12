package com.ibroadlink.screen;




import com.ibroadlink.library.aidlink.annotation.RemoteInterface;

import java.util.List;

/**
 * Created by codezjx on 2018/3/14.<br/>
 */
@RemoteInterface
public interface IRemoteTask {

    int remoteCalculate(int a, int b);

    List<ParcelableObj> getDatas();
    
}
