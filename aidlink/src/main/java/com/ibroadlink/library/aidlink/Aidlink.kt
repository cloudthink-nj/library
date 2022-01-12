package com.ibroadlink.library.aidlink

import android.content.ServiceConnection
import android.content.Intent
import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import android.os.RemoteException
import com.ibroadlink.library.aidlink.adapter.CallAdapterFactory
import com.ibroadlink.library.aidlink.adapter.DefaultFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * AndLinker adapts a Java interface to IPC calls by using annotations on the declared methods to
 * define how requests are made. Create instances using [ the builder][Builder] and pass your interface to [.create] to generate an implementation.
 */
class Aidlink(
    private val mContext: Context,
    private val mPackageName: String,
    private val mAction: String? = null,
    private val mClassName: String? = null,
    private val mAdapterFactories: List<CallAdapter.Factory> = listOf(CallAdapterFactory.create())
) {
    private val serviceMethodCache: MutableMap<Method, ServiceMethod?> = ConcurrentHashMap()
    private val mServiceConnection: ServiceConnection
    private val mInvoker: Invoker = Invoker()
    private val mDispatcher: Dispatcher = Dispatcher()
    private var mTransferService: ITransfer? = null
    private val mCallback: ICallback
    var mBindCallback: BindCallback? = null

    /**
     * Return the remote service bind state.
     */
    val isBind: Boolean
        get() = mTransferService != null

    /**
     * Create an implementation defined by the remote service interface.
     */
    // Single-interface proxy creation guarded by parameter safety.
    fun <T> create(service: Class<T>): T {
        Utils.validateServiceInterface(service)
        return Proxy.newProxyInstance(service.classLoader, arrayOf<Class<*>>(service),
            object : InvocationHandler {
                @Throws(Throwable::class)
                override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.declaringClass == Any::class.java) {
                        return method.invoke(this, *args)
                    }
                    val serviceMethod = loadServiceMethod(method)
                    val remoteCall = RemoteCall(mTransferService, serviceMethod, args, mDispatcher)
                    return serviceMethod?.callAdapter?.adapt(remoteCall)
                }
            }) as T
    }

    /**
     * Connect to the remote service.
     */
    fun bind() {
        check(!Utils.isStringBlank(mPackageName)) { "Package name required." }
        check(
            !(Utils.isStringBlank(mAction) && Utils.isStringBlank(
                mClassName
            ))
        ) { "You must set one of the action or className." }
        if (isBind) {
            Logger.d(TAG, "Already bind, just return.")
            return
        }
        val intent = Intent()
        if (!Utils.isStringBlank(mAction)) {
            intent.action = mAction
        } else if (!Utils.isStringBlank(mClassName)) {
            intent.setClassName(mPackageName, mClassName!!)
        }
        // After android 5.0+, service Intent must be explicit.
        intent.setPackage(mPackageName)
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Disconnect from the remote service.
     */
    fun unbind() {
        if (!isBind) {
            Logger.d(TAG, "Already unbind, just return.")
            return
        }
        mContext.unbindService(mServiceConnection)
        handleUnBind()
    }

    /**
     * Register client interface implementation called by remote service.
     */
    fun registerObject(target: Any?) {
        mInvoker.registerObject(target)
    }

    /**
     * Unregister client interface implementation.
     */
    fun unRegisterObject(target: Any?) {
        mInvoker.unRegisterObject(target)
    }


    fun findCallAdapter(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *> {
        Utils.checkNotNull(returnType, "returnType == null")
        Utils.checkNotNull(annotations, "annotations == null")
        var i = 0
        val count = mAdapterFactories.size
        while (i < count) {
            val adapter = mAdapterFactories[i][returnType, annotations]
            if (adapter != null) {
                return adapter
            }
            i++
        }
        return DefaultFactory[returnType, annotations]
    }

    private fun createServiceConnection(): ServiceConnection {
        return object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Logger.d(TAG, "onServiceConnected:$name service:$service")
                mTransferService = ITransfer.Stub.asInterface(service)
                fireOnBind()
                try {
                    mTransferService?.register(mCallback)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Logger.d(TAG, "onServiceDisconnected:$name")
                handleUnBind()
            }
        }
    }

    private fun handleUnBind() {
        if (mTransferService == null) {
            Logger.e(TAG, "Error occur, TransferService was null when service disconnected.")
            fireOnUnBind()
            return
        }
        try {
            mTransferService!!.unRegister(mCallback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        mTransferService = null
        fireOnUnBind()
    }

    private fun fireOnBind() {
        mBindCallback?.onBind()
    }

    private fun fireOnUnBind() {
        mBindCallback?.onUnBind()
    }

    private fun createCallback(): ICallback {
        return object : ICallback.Stub() {
            @Throws(RemoteException::class)
            override fun callback(request: Request): Response {
                Logger.d(TAG, "Receive callback in client:$request")
                return mInvoker.invoke(request)
            }
        }
    }

    private fun loadServiceMethod(method: Method): ServiceMethod? {
        var result = serviceMethodCache[method]
        if (result != null) {
            return result
        }
        synchronized(serviceMethodCache) {
            result = serviceMethodCache[method]
            if (result == null) {
                result = ServiceMethod.Builder(this, method).build()
                serviceMethodCache[method] = result
            }
        }
        return result
    }

    /**
     * Interface definition for a callback to be invoked when linker is bind or unBind to the service.
     */
    interface BindCallback {
        /**
         * Called when a connection to the remote service has been established, now you can execute the remote call.
         */
        fun onBind()

        /**
         * Called when a connection to the remote service has been lost, any remote call will not execute.
         */
        fun onUnBind()
    }

    companion object {
        private const val TAG = "Aidlink"

        /**
         * Method to enable or disable internal logger
         */
        fun enableLogger(enable: Boolean) {
            Logger.sEnable = enable
        }
    }

    init {
        mServiceConnection = createServiceConnection()
        mCallback = createCallback()
    }
}