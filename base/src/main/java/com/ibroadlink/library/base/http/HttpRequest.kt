package com.ibroadlink.library.base.http

import android.annotation.SuppressLint
import com.blankj.utilcode.util.Utils
import com.ibroadlink.library.base.extend.GSON
import com.ibroadlink.library.base.http.base.BaseHttpRequest
import com.ibroadlink.library.base.http.base.CoroutineCallAdapterFactory
import com.ibroadlink.library.base.http.interceptor.CacheInterceptor
import com.ibroadlink.library.base.http.interceptor.logging.LogInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * 描述　: 网络请求构建器，继承BasenetworkApi 并实现setHttpClientBuilder/setRetrofitBuilder方法，
 * 在这里可以添加拦截器，设置构造器可以对Builder做任意操作
 */

class HttpRequest : BaseHttpRequest() {

    companion object {
        val INSTANCE: HttpRequest by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            HttpRequest()
        }
    }

    override fun getBaseUrl(): String {
        return "https://www.baidu.com/"
    }

    /**
     * 实现重写父类的setHttpClientBuilder方法，
     * 在这里可以添加拦截器，可以对 OkHttpClient.Builder 做任意操作
     */
    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        builder.apply {
            //设置缓存配置 缓存最大10M
            cache(Cache(File(Utils.getApp().cacheDir, "http_cache"), 10 * 1024 * 1024.toLong()))
            // 日志拦截器
            addInterceptor(LogInterceptor())
            //超时时间 连接、读、写
            connectTimeout(30, TimeUnit.SECONDS)
            callTimeout(120, TimeUnit.SECONDS)
            pingInterval(10, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            sslSocketFactory(createSSLSocketFactory()!!, TrustAllManager())
            hostnameVerifier(TrustAllHostnameVerifier())
        }
        return builder
    }

    /**
     * 实现重写父类的setRetrofitBuilder方法，
     * 在这里可以对Retrofit.Builder做任意操作，比如添加GSON解析器，protobuf等
     */
    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.apply {
            addConverterFactory(GsonConverterFactory.create(GSON))
            addCallAdapterFactory(CoroutineCallAdapterFactory())
        }
    }

    @SuppressLint("TrulyRandom")
    private fun createSSLSocketFactory(): SSLSocketFactory? {
        var sSLSocketFactory: SSLSocketFactory? = null
        try {
            val sc = SSLContext.getInstance("TLS")
            sc.init(
                null, arrayOf<TrustManager>(TrustAllManager()),
                SecureRandom()
            )
            sSLSocketFactory = sc.socketFactory
        } catch (ignored: Exception) {
        }
        return sSLSocketFactory
    }

    @SuppressLint("CustomX509TrustManager")
    private class TrustAllManager : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return emptyArray()
        }
    }

    class TrustAllHostnameVerifier : HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }
}



