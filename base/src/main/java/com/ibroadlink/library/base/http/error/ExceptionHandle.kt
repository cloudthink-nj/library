package com.ibroadlink.library.base.http.error

import android.net.ParseException
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.ibroadlink.library.base.http.data.ErrorInfo
import com.ibroadlink.library.base.callback.eventbus.LiveDataBus
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * 描述　: 根据异常返回相关的错误信息工具类
 */
object ExceptionHandle {
    const val UNIFY_EXCEPTION_EVENT = "unify_exception_event"
    private val errorList: MutableList<ErrorInfo> = mutableListOf()

    fun addErrorList(vararg error: ErrorInfo) {
        errorList.addAll(error)
    }

    fun handleException(e: Throwable?): AppException {
        val ex = e?.let {
            when (it) {
                is HttpException -> {
                    AppException(ErrorInfo(1002, "网络连接错误，请稍后重试"), e)
                }
                is JsonParseException, is JSONException, is ParseException, is MalformedJsonException -> {
                    AppException(ErrorInfo(1001, "解析错误，请稍后再试"), e)
                }
                is ConnectException -> {
                    AppException(ErrorInfo(1002, "网络连接错误，请稍后重试"), e)
                }
                is javax.net.ssl.SSLException -> {
                    AppException(ErrorInfo(1004, "证书出错，请稍后再试"), e)
                }
                is ConnectTimeoutException -> {
                    AppException(ErrorInfo(1006, "网络连接超时，请稍后重试"), e)
                }
                is java.net.SocketTimeoutException -> {
                    AppException(ErrorInfo(1006, "网络连接超时，请稍后重试"), e)
                }
                is java.net.UnknownHostException -> {
                    AppException(ErrorInfo(1006, "网络连接超时，请稍后重试"), e)
                }
                is AppException -> {
                    val find = errorList.find { errorInfo ->
                        errorInfo.code == it.errCode
                    }
                    if (find == null) {
                        AppException(it.errCode, it.errorMsg)
                    } else {
                        AppException(find, e)
                    }
                }
                else -> {
                    AppException(ErrorInfo(1000, "请求失败，请稍后再试"), e)
                }
            }
        } ?: AppException(ErrorInfo(1000, "请求失败，请稍后再试"), e)
        LiveDataBus.send(UNIFY_EXCEPTION_EVENT, ex)
        return ex
    }
}