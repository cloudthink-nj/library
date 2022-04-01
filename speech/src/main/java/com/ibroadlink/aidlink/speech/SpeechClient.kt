package com.ibroadlink.aidlink.speech

import android.content.Context
import com.ibroadlink.aidlink.speech.service.IRemoteCallback
import com.ibroadlink.aidlink.speech.service.IRemoteService
import com.ibroadlink.library.aidlink.Aidlink
import com.ibroadlink.library.aidlink.adapter.OriginalCallAdapterFactory

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2022/2/25 18:23
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: SpeechManager
 */
class SpeechClient @JvmOverloads constructor(
    context: Context,
    private val packageName: String,
    private val remoteCallback: IRemoteCallback,
    action: String = "$packageName.SpeechService",
) {

    private var mLinker: Aidlink = Aidlink.Builder(context)
        .packageName(packageName)
        .action(action)
        .addCallAdapterFactory(OriginalCallAdapterFactory.create())
        .build()

    private var mService: IRemoteService? = null

    val isBound: Boolean
        get() = mLinker.isBind

    private val mBindCallback: Aidlink.BindCallback = object : Aidlink.BindCallback {
        override fun onBind() {
            mService = mLinker.create(IRemoteService::class.java)
            mService?.setCallback(remoteCallback)
            remoteCallback.onServiceBind(packageName)
        }

        override fun onUnBind() {
            mService = null
            remoteCallback.onServiceUnBind(packageName)
        }
    }

    fun enableLogger(enable: Boolean) {
        Aidlink.enableLogger(enable)
    }

    fun handleSkill(nativeApi: String?, data: String?) {
        mService?.handleSkill(nativeApi, data)
    }

    fun querySkillVocabs() {
        mService?.querySkillVocabs()
    }

    fun getOauthToken() {
        mService?.getOauthToken()
    }

    fun bindService() {
        mLinker.bind()
    }

    fun unbindService() {
        mService?.setCallback(null)
        mLinker.unRegisterObject(remoteCallback)
        mLinker.unbind()
        mLinker.setBindCallback(null)
        mService = null
    }

    init {
        mLinker.setBindCallback(mBindCallback)
        mLinker.registerObject(remoteCallback)
    }
}