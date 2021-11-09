package com.ibroadlink.library.base.http.base

import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * 描述　: 网络请求构建器基类
 */
abstract class BaseHttpRequest {

    fun <T> getApi(serviceClass: Class<T>): T {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(okHttpClient)
        return setRetrofitBuilder(retrofitBuilder).build().create(serviceClass)
    }

    /**
     * 设置base url
     */
    abstract fun getBaseUrl(): String

    /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    abstract fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder

    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，Protocol
     */
    abstract fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder

    /**
     * 配置http
     */
    val okHttpClient: OkHttpClient
        get() {
            val builder = setHttpClientBuilder(OkHttpClient.Builder())
            return builder.build()
        }
}