package com.ibroadlink.library.base.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/9/8 11:29
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: ViewBindingBaseActivity
 */
abstract class ViewBindingBaseActivity<T : ViewBinding> : AppCompatActivity() {

    private lateinit var _binding: T
    protected val mBinding get() = _binding

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(_binding.root)
        initView(savedInstanceState)
    }

    protected abstract fun getViewBinding(): T
}