package com.ibroadlink.library.aidlink.adapter

import android.os.Handler
import android.os.Looper
import com.ibroadlink.library.aidlink.Call
import com.ibroadlink.library.aidlink.CallAdapter
import com.ibroadlink.library.aidlink.Callback
import java.lang.IllegalStateException
import java.lang.reflect.Type
import java.util.concurrent.Executor

/**
 * A [call adapter][CallAdapter.Factory] which uses the original [Call], just return as is.
 */
class CallAdapterFactory(private val mCallbackExecutor: Executor) : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *>? {
        return if (getRawType(returnType) != Call::class.java) {
            null
        } else CallAdapter<Any?, Call<*>?> { call -> // Return executor wrapper call
            ExecutorCallbackCall(mCallbackExecutor, call)
        }
    }

    internal class ExecutorCallbackCall<T>(
        val mCallbackExecutor: Executor,
        val mDelegate: Call<T>
    ) : Call<T> {
        override fun execute(): T {
            return mDelegate.execute()
        }

        override fun enqueue(callback: Callback<T>) {
            mDelegate.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: T) {
                    mCallbackExecutor.execute {
                        if (mDelegate.isCanceled) {
                            // Emulate behavior of throwing/delivering an IOException on cancellation.
                            callback.onFailure(
                                this@ExecutorCallbackCall,
                                IllegalStateException("Already canceled")
                            )
                        } else {
                            callback.onResponse(this@ExecutorCallbackCall, response)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    mCallbackExecutor.execute { callback.onFailure(this@ExecutorCallbackCall, t) }
                }
            })
        }

        override fun isExecuted(): Boolean {
            return mDelegate.isExecuted
        }

        override fun cancel() {
            mDelegate.cancel()
        }

        override fun isCanceled(): Boolean {
            return mDelegate.isCanceled
        }
    }

    internal class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            handler.post(r)
        }
    }

    companion object {
        /**
         * Create [CallAdapterFactory] with default Android main thread executor.
         */
        fun create(): CallAdapterFactory {
            return CallAdapterFactory(MainThreadExecutor())
        }

        /**
         * Create [CallAdapterFactory] with specify [Executor]
         * @param callbackExecutor The executor on which [Callback] methods are invoked
         * when returning [Call] from your service method.
         */
        fun create(callbackExecutor: Executor): CallAdapterFactory {
            return CallAdapterFactory(callbackExecutor)
        }
    }
}