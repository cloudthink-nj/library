package com.ibroadlink.library.base.http.network

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.ibroadlink.library.base.utils.BLConstantUtils
import com.ibroadlink.library.base.utils.BLReceiverUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.Utils
import com.ibroadlink.library.base.callback.eventbus.LiveDataBus


/**
 * 描述　: 网络变化管理者
 */
@SuppressLint("MissingPermission")
object NetworkCheckManager {
    const val NETWORK_CHANGED_EVENT = "network_changed_event"
    private val mPingIPList = listOf("114.114.114.114", "223.5.5.5", "180.76.76.76")
    var isAvailable: Boolean = false
        private set

    private const val MESSAGE_NETWORK_AVAILABLE = 40000

    private val mNetworkChangeReceiver =
        object : BLReceiverUtils(
            ConnectivityManager.CONNECTIVITY_ACTION
        ) {
            override fun onReceiveAction(action: String, intent: Intent) {
                mHandler.sendEmptyMessage(MESSAGE_NETWORK_AVAILABLE)
            }
        }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_NETWORK_AVAILABLE -> {
                    checkNetworkAvailable()
                }
                else -> {
                }
            }
        }
    }

    fun init() {
        mNetworkChangeReceiver.register(Utils.getApp())
    }

    private fun checkNetworkAvailable() {
        mHandler.removeMessages(MESSAGE_NETWORK_AVAILABLE)
        NetworkUtils.isAvailableByPingAsync(mPingIPList.random()) {
            LogUtils.i("ping dns result: $it")
            mHandler.sendEmptyMessageDelayed(
                MESSAGE_NETWORK_AVAILABLE,
                if (it)
                    BLConstantUtils.TWO_MINUTE_DURATION
                else
                    BLConstantUtils.THIRTY_SECOND_DURATION
            )
            if (isAvailable != it) {
                isAvailable = it
                LiveDataBus.send(NETWORK_CHANGED_EVENT, it)
            }
        }
    }
}