package com.ibroadlink.library.aidl.server

import android.graphics.Bitmap

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/6/3 14:26
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: IRequestInterface
 */
interface IRequestInterface {
    fun requestAction(action: String, data: String?)

    fun getBitmap(action: String, data: String?): List<Bitmap>
}