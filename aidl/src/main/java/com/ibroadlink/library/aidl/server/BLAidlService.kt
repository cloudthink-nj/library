package com.ibroadlink.library.aidl.server

import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.ibroadlink.library.base.app.CoroutineBaseService

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/21 9:17
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: AidlService
 */
abstract class BLAidlService : CoroutineBaseService(), IRequestInterface {

    override fun onCreate() {
        super.onCreate()
        initImpl(AidlServiceImpl(this))
    }

    override fun getBitmap(action: String, data: String?): List<Bitmap> {
        return emptyList()
    }

    override fun onBind(intent: Intent): IBinder = mBinderImpl

    companion object {
        lateinit var mBinderImpl: AidlServiceImpl

        fun initImpl(impl: AidlServiceImpl) {
            mBinderImpl = impl
        }
    }
}