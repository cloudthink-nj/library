package com.ibroadlink.library.base.http.data

import com.ibroadlink.library.base.http.base.BaseResponse

/**
 * 描述　:服务器返回数据的基类
 * 如果项目中有基类，那美滋滋，可以继承BaseResponse，请求时框架可以自动脱壳，自动判断是否请求成功：
 * 1.继承 BaseResponse
 * 2.重写isSuccess 方法，编写业务需求，根据自己的条件判断数据是否请求成功
 * 3.重写 getResponseCode、getResponseData、getResponseMsg方法，传入你的 code data msg
 */
open class BaseResult<T>(
    val code: Int = -1,
    val msg: String,
    val data: T
) : BaseResponse<T>() {

    override fun isSuccess() = getResponseCode() == 0

    override fun getResponseCode() = code

    override fun getResponseData() = data

    override fun getResponseMsg() = msg

}