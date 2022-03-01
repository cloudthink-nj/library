package com.ibroadlink.aidlink.speech.service

import com.ibroadlink.library.aidlink.annotation.Callback
import com.ibroadlink.library.aidlink.annotation.RemoteInterface

@RemoteInterface
interface IRemoteService {

    fun handleSkill(nativeApi: String?, data: String?)

    fun querySkillVocabs()

    fun setCallback(@Callback callback: IRemoteCallback?)
}