package com.ibroadlink.library.base.http.data

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/30 10:14
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: xxx
 */
class ErrorInfo (
    val code: Int,
    val msg: String,
    val desc: String? = null)