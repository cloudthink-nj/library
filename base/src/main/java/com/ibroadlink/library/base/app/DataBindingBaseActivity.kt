package com.ibroadlink.library.base.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.ibroadlink.library.base.extend.getVmClazz

/**
 * @Author: Broadlink lvzhaoyang
 * @CreateDate: 2021/10/19 17:00
 * @Email: zhaoyang.lv@broadlink.com.cn
 * @Description: 包含ViewModel 和Databind ViewModelActivity基类，把ViewModel 和Databind注入进来了
 */
abstract class DataBindingBaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : AppCompatActivity() {

    lateinit var mDataBind: DB

    lateinit var mViewModel: VM

    abstract fun layoutId(): Int

    abstract fun initView(savedInstanceState: Bundle?)

    abstract fun showLoading(message: String = "请求网络中...")

    abstract fun dismissLoading()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBind = DataBindingUtil.setContentView(this, layoutId())
        mDataBind.lifecycleOwner = this
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        mViewModel = createViewModel()
        registerUiChange()
        initView(savedInstanceState)
        createObserver()
    }

    /**
     * 创建viewModel
     */
    private fun createViewModel(): VM {
        return ViewModelProvider(this).get(getVmClazz(this))
    }

    /**
     * 创建LiveData数据观察者
     */
    abstract fun createObserver()

    /**
     * 注册UI 事件
     */
    private fun registerUiChange() {
        //显示弹窗
        mViewModel.loadingChange.showDialog.observe(this, {
            showLoading(it)
        })
        //关闭弹窗
        mViewModel.loadingChange.dismissDialog.observe(this, {
            dismissLoading()
        })
    }

    /**
     * 将非该Activity绑定的ViewModel添加 loading回调 防止出现请求时不显示 loading 弹窗bug
     * @param viewModels Array<out BaseViewModel>
     */
    protected fun addLoadingObserve(vararg viewModels: BaseViewModel){
        viewModels.forEach {viewModel ->
            //显示弹窗
            viewModel.loadingChange.showDialog.observe(this, {
                showLoading(it)
            })
            //关闭弹窗
            viewModel.loadingChange.dismissDialog.observe(this, {
                dismissLoading()
            })
        }
    }
}