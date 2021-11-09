package com.ibroadlink.library.base.http.coil

import com.ibroadlink.library.base.http.HttpRequest
import coil.ImageLoader
import coil.request.CachePolicy
import com.blankj.utilcode.util.Utils

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/3/17 10:55
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: coil的ImageLoader
 */
object ImageLoader {
    val imageLoader = ImageLoader.Builder(Utils.getApp())
        .crossfade(true)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .networkCachePolicy(CachePolicy.ENABLED)
        .okHttpClient(HttpRequest.INSTANCE.okHttpClient)
        .build()
}