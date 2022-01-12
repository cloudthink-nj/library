package com.ibroadlink.library.aidlink.annotation

/**
 * Indicate a remote call does not block, it simply sends the transaction data and immediately
 * returns, same as "oneway" tag in AIDL.
 */
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class OneWay