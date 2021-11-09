package com.ibroadlink.library.aidl.server

import android.os.RemoteCallbackList
import android.os.RemoteException
import com.ibroadlink.library.aidl.IAidlService
import com.ibroadlink.library.aidl.IAidlCallback
import com.blankj.utilcode.util.ThreadUtils

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/6/3 10:16
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: AidlServiceImpl
 */
open class AidlServiceImpl(private val mRequest: IRequestInterface) : IAidlService.Stub() {

    private val mCallbackList = RemoteCallbackList<IAidlCallback>()

    @Throws(RemoteException::class)
    override fun addCallback(cb: IAidlCallback) {
        mCallbackList.register(cb)
    }

    @Throws(RemoteException::class)
    override fun delCallback(cb: IAidlCallback) {
        mCallbackList.unregister(cb)
    }

    @Throws(RemoteException::class)
    override fun requestAction(action: String, data: String?) {
        mRequest.requestAction(action, data)
    }

    fun replyMessage(action: String, data: String?) {
        ThreadUtils.getSinglePool().execute {
            try {
                val n = mCallbackList.beginBroadcast()
                for (i in 0 until n) {
                    val callback = mCallbackList.getBroadcastItem(i)
                    callback.onCallback(action, data)
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            } finally {
                mCallbackList.finishBroadcast()
            }
        }
    }
}