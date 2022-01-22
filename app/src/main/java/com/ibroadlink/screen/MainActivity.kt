package com.ibroadlink.screen

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ibroadlink.library.aidlink.Aidlink
import com.ibroadlink.library.aidlink.adapter.OriginalCallAdapterFactory
import com.ibroadlink.library.aidlink.annotation.RemoteInterface
import com.ibroadlink.library.base.app.ViewBindingBaseActivity
import com.ibroadlink.library.base.extend.clickWithTrigger
import com.ibroadlink.screen.databinding.ActivityBindingBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ViewBindingBaseActivity<ActivityBindingBinding>(), Aidlink.BindCallback {
    private val TAG = "BindingActivity"
    private val REMOTE_SERVICE_PKG = "com.ibroadlink.screen"
    val REMOTE_SERVICE_ACTION = "com.example.andlinker.REMOTE_SERVICE_ACTION"

    private lateinit var mLinker: Aidlink
    private var mRemoteService: IRemoteService? = null
    private var mRemoteTask: IRemoteTask? = null

    override fun initView(savedInstanceState: Bundle?) {
        Aidlink.enableLogger(true)
        mLinker = Aidlink.Builder(this)
            .packageName(REMOTE_SERVICE_PKG)
            .action(REMOTE_SERVICE_ACTION)
            .addCallAdapterFactory(OriginalCallAdapterFactory.create()) // Basic
            .build()
        mLinker.setBindCallback(this)
        mLinker.registerObject(mRemoteCallback)
        mLinker.bind()

        mBinding.run {
            btnPid.clickWithTrigger {
                Toast.makeText(
                    this@MainActivity,
                    "Server pid: ${mRemoteService?.pid}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnBasicTypes.clickWithTrigger {
                mRemoteService?.basicTypes(1, 2L, true, 3.0f, 4.0, "str")
            }

            btnCallAdapter.clickWithTrigger {
                launch {
                    val remoteCalculate = withContext(IO) {
                        mRemoteTask?.remoteCalculate(10, 20)
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "remoteCalculate: $remoteCalculate",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnRxjava2CallAdapter.clickWithTrigger {
                launch {
                    val datas = withContext(IO) {
                        mRemoteTask?.getDatas()
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "getDatas: $datas",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            btnCallback.clickWithTrigger {
                mRemoteService?.registerCallback(mRemoteCallback)
            }
            btnBitmap.clickWithTrigger {
                val bitmap = mRemoteService?.bitmap
                ivIcon.setImageBitmap(bitmap)
            }
        }
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun getViewBinding(): ActivityBindingBinding =
        ActivityBindingBinding.inflate(layoutInflater)

    override fun onBind() {
        Log.d(TAG, "AndLinker onBind()")
        mRemoteService = mLinker.create(IRemoteService::class.java)
        mRemoteTask = mLinker.create(IRemoteTask::class.java)
    }

    override fun onUnBind() {
        Log.d(TAG, "AndLinker onUnBind()")
        mRemoteService = null
        mRemoteTask = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mLinker.unRegisterObject(mRemoteCallback)
        mLinker.unbind()
        mLinker.setBindCallback(null)
    }

    private val mRemoteCallback: IRemoteCallback = object : IRemoteCallback {
        override fun onStart() {
            Log.d(TAG, "Server callback onStart!")
        }

        override fun onValueChange(value: Int) {
            // Invoke when server side callback
            Toast.makeText(
                this@MainActivity,
                "Server callback value: $value",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    /**
     * Copy the original interface, wrap the return type of the method, keep the original interface name and method name.
     */
    @RemoteInterface
    interface IRemoteTask {
        fun remoteCalculate(a: Int, b: Int): Int

        fun getDatas(): List<ParcelableObj>?
    }
}