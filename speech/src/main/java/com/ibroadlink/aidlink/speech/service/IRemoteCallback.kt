package com.ibroadlink.aidlink.speech.service

import com.ibroadlink.library.aidlink.annotation.In
import com.ibroadlink.library.aidlink.annotation.RemoteInterface

@RemoteInterface
interface IRemoteCallback {

    fun onUpdateVocabs(@In vocabsMap: Map<String, List<String>>)

    fun onReplyResult(nativeApi: String, result: String)

    fun onServiceBind(packageName: String)

    fun onServiceUnBind(packageName: String)
}