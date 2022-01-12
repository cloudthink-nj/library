package com.ibroadlink.library.aidlink

import android.os.RemoteCallbackList
import android.os.RemoteException

/**
 * Created by codezjx on 2017/9/13.<br></br>
 */
internal class AidlinkBinderImpl : ITransfer.Stub(), AidlinkBinder {
    private val mCallbackList: RemoteCallbackList<ICallback>
    private val mInvoker: Invoker = Invoker()
    override fun registerObject(target: Any?) {
        mInvoker.registerObject(target)
    }

    override fun unRegisterObject(target: Any?) {
        mInvoker.unRegisterObject(target)
    }

    @Throws(RemoteException::class)
    override fun execute(request: Request): Response {
        for (wrapper in request.argsWrapper) {
            Logger.d(
                TAG, "Receive param, value:" + wrapper.param
                        + " type:" + if (wrapper.param != null) wrapper.param.javaClass else "null"
            )
        }
        Logger.d(TAG, "Receive request:" + request.methodName)
        return mInvoker.invoke(request)
    }

    @Throws(RemoteException::class)
    override fun register(callback: ICallback) {
        val pid = getCallingPid()
        Logger.d(TAG, "register callback:$callback pid:$pid")
        mCallbackList.register(callback, pid)
    }

    @Throws(RemoteException::class)
    override fun unRegister(callback: ICallback) {
        val pid = getCallingPid()
        Logger.d(TAG, "unRegister callback:$callback pid:$pid")
        mCallbackList.unregister(callback)
    }

    companion object {
        private const val TAG = "LinkerBinder"
    }

    init {
        mCallbackList = mInvoker.callbackList
    }
}