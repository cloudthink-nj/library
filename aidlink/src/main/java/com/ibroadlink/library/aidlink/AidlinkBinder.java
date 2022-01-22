package com.ibroadlink.library.aidlink;

import android.os.IBinder;

/**
 * AndLinker {@link IBinder} object to return in {@link android.app.Service#onBind(android.content.Intent)} method.
 */
public interface AidlinkBinder extends IBinder {

    /**
     * Register service interface implementation.
     */
    void registerObject(Object target);

    /**
     * Unregister service interface implementation.
     */
    void unRegisterObject(Object target);

    /**
     * {@link AidlinkBinder} factory class.
     */
    final class Factory {

        private Factory() {
            
        }
        
        /**
         * Factory method to create the {@link AidlinkBinder} impl instance.
         */
        public static AidlinkBinder newBinder() {
            // Return inner package access LinkerBinder, prevent exposed.
            return new AidlinkBinderImpl();
        }
    }
    
}
