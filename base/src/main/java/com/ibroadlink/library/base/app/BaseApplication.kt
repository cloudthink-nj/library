package com.ibroadlink.library.base.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.multidex.MultiDexApplication
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ProcessUtils
import com.blankj.utilcode.util.Utils
import com.ibroadlink.library.base.BuildConfig
import com.ibroadlink.library.base.http.network.NetworkCheckManager
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel

open class BaseApplication : MultiDexApplication(), ViewModelStoreOwner {

    private lateinit var mAppViewModelStore: ViewModelStore

    private var mFactory: ViewModelProvider.Factory? = null

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    override fun onCreate() {
        super.onCreate()
        mAppViewModelStore = ViewModelStore()

        initApp(this)

        val processName = ProcessUtils.getCurrentProcessName()
        if (processName == packageName) {
            // 主进程初始化
            onMainProcessInit()
        } else {
            // 其他进程初始化
            processName?.let { onOtherProcessInit(it) }
        }
    }

    /**
     * 通常来说应用只有一个进程，进程名称是当前的包名，你需要针对这个进程做一些初始化。
     * 如果你引入了第三方服务，比如地图，推送什么的，很可能对方是开了个额外的进程在跑的，
     * 这个时候就没必要初始化你的资源了，因为它根本用不上你的。
     *
     * 如果开了多进程，那么就复写 [onCreate] 自己判断要怎么初始化资源吧
     */
    open fun onMainProcessInit() {}

    /**
     * 其他进程初始化，[processName] 进程名
     */
    open fun onOtherProcessInit(processName: String) {}

    /**
     * 获取一个全局的ViewModel
     */
    fun getAppViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, this.getAppFactory())
    }

    private fun getAppFactory(): ViewModelProvider.Factory {
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(this)
        }
        return mFactory as ViewModelProvider.Factory
    }

    companion object {
        lateinit var appContext: Application
        lateinit var mmkv: MMKV

        fun initApp(app: Application) {
            appContext = app
            Utils.init(app)
            mmkv = if (BuildConfig.DEBUG) {
                MMKV.initialize(app, MMKVLogLevel.LevelDebug)
                MMKV.mmkvWithID("app")
            } else {
                MMKV.initialize(app, MMKVLogLevel.LevelError)
                MMKV.mmkvWithID(
                    "app",
                    MMKV.SINGLE_PROCESS_MODE,
                    AppUtils.getAppSignaturesMD5()[0]
                )
            }
        }
    }
}