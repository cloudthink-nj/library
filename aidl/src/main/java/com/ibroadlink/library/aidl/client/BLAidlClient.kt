package com.ibroadlink.library.aidl.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.os.RemoteException
import com.blankj.utilcode.util.*
import com.ibroadlink.library.aidl.IAidlCallback
import com.ibroadlink.library.aidl.IAidlService

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/21 9:31
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: AidlClient
 */
abstract class BLAidlClient {

    private var mAidlBinder: IAidlService? = null

    private val serviceCallback: IAidlCallback = object : IAidlCallback.Stub() {

        @Throws(RemoteException::class)
        override fun onCallback(action: String, data: String?) {
            onHandleAction(action, data ?: "")
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mAidlBinder = IAidlService.Stub.asInterface(service)
            mAidlBinder?.run {
                try {
                    addCallback(serviceCallback)
                    //给binder设置死忙代理，当Binder死忙时就可以收到通知
                    service.linkToDeath(mDeathRecipient, 0)
                    onBinderConnected()
                } catch (e: Exception) {
                    LogUtils.e(e)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mAidlBinder = null
        }
    }
    private val mDeathRecipient: IBinder.DeathRecipient = object : IBinder.DeathRecipient {
        override fun binderDied() {
            //移除之前绑定的代理并重新绑定远程服务
            mAidlBinder?.asBinder()?.unlinkToDeath(this, 0)
            mAidlBinder = null
            onBinderDied()
        }
    }

    val isBound: Boolean
        get() = mAidlBinder != null

    fun request(action: String, data: String?) {
        ThreadUtils.getCachedPool().execute {
            try {
                mAidlBinder?.requestAction(action, data)
            } catch (e: RemoteException) {
                LogUtils.e(e)
            }
        }
    }

    fun bindService(action: String, packageName: String) {
        if (mAidlBinder == null && AppUtils.isAppInstalled(packageName)) {
            val intent = Intent(action)
            intent.setPackage(packageName)
            Utils.getApp().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService() {
        mAidlBinder?.run {
            delCallback(serviceCallback)
            ServiceUtils.unbindService(mServiceConnection)
        }
        mAidlBinder = null
    }

    abstract fun onHandleAction(action: String, data: String)

    abstract fun onBinderDied()

    abstract fun onBinderConnected()
}