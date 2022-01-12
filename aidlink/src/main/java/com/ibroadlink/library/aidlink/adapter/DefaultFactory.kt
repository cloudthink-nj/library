package com.ibroadlink.library.aidlink.adapter

import com.ibroadlink.library.aidlink.Call
import com.ibroadlink.library.aidlink.CallAdapter
import java.lang.reflect.Type

/**
 * Default [call adapter][CallAdapter.Factory] which adapt [Call] to the execute result.
 */
object DefaultFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>): CallAdapter<*, *> {
        return CallAdapter<Any?, Any?> { call -> // Return the result
            call.execute()
        }
    }
}