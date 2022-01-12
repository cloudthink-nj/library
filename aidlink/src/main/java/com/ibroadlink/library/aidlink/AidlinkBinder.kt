package com.ibroadlink.library.aidlink

import android.os.IBinder

/**
 * AndLinker [IBinder] object to return in [android.app.Service.onBind] method.
 */
interface AidlinkBinder : IBinder {
    /**
     * Register service interface implementation.
     */
    fun registerObject(target: Any?)

    /**
     * Unregister service interface implementation.
     */
    fun unRegisterObject(target: Any?)

    /**
     * [AidlinkBinder] factory class.
     */
    object Factory {
        /**
         * Factory method to create the [AidlinkBinder] impl instance.
         */
        @JvmStatic
        fun newBinder(): AidlinkBinder {
            // Return inner package access LinkerBinder, prevent exposed.
            return AidlinkBinderImpl()
        }
    }
}