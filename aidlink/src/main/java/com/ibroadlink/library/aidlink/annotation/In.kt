package com.ibroadlink.library.aidlink.annotation

/**
 * Directional tag indicating which way the data goes, same as "in" tag in AIDL.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class In