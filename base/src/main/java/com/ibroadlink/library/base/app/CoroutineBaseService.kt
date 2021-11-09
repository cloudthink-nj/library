package com.ibroadlink.library.base.app

import android.annotation.SuppressLint
import android.app.Service
import android.os.Build
import android.os.PowerManager
import kotlinx.coroutines.*

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/20 18:36
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: 协程service
 */
abstract class CoroutineBaseService : Service(), CoroutineScope by MainScope() {
    private lateinit var mWakeLock: PowerManager.WakeLock

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.simpleName)
        mWakeLock.acquire()
    }

    fun <T> launchMain(
        block: () -> T
    ) {
        launch(Dispatchers.Main) {
            block()
        }
    }

    fun <T> launchIO(
        block: suspend () -> T,
        success: (T) -> Unit = {},
        error: (Throwable) -> Unit = {}
    ) {
        launch(Dispatchers.Default) {
            runCatching {
                withContext(Dispatchers.IO) {
                    block()
                }
            }.onSuccess {
                success(it)
            }.onFailure {
                error(it)
            }
        }
    }

    fun <T> launch(
        block: () -> T
    ) {
        launch(Dispatchers.Default) {
            block()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        mWakeLock.release()
    }
}