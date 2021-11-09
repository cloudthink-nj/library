package com.ibroadlink.library.base.http.error

import com.ibroadlink.library.base.http.data.ErrorInfo

/**
 * 描述　:自定义错误信息异常
 */
class AppException : Exception {

    var errorMsg: String //错误消息
    var errCode: Int = 0 //错误码
    var errorDesc: String? //错误描述

    constructor(error: ErrorInfo, e: Throwable?) : super(e) {
        errCode = error.code
        errorMsg = error.msg
        errorDesc = error.desc
    }

    constructor(errCode: Int, errorMsg: String?, errorDesc: String? = "") : super(errorMsg) {
        this.errorMsg = errorMsg ?: "请求失败，请稍后再试"
        this.errCode = errCode
        this.errorDesc = errorDesc
    }
}