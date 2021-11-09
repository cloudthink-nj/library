package com.ibroadlink.library.base.http.interceptor

import android.annotation.SuppressLint
import com.ibroadlink.library.base.http.network.NetworkCheckManager
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 描述　: 缓存拦截器
 * @param day 缓存天数 默认7天
 */
class CacheInterceptor(var day: Int = 7) : Interceptor {
    @SuppressLint("MissingPermission")
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (!NetworkCheckManager.isAvailable) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        val response = chain.proceed(request)
        if (!NetworkCheckManager.isAvailable) {
            val maxAge = 60 * 60
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
                .build()
        } else {
            val maxStale = 60 * 60 * 24 * day // tolerate 4-weeks stale
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}