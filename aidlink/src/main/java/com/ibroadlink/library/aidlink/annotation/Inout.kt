package com.ibroadlink.library.aidlink.annotation

/**
 * Directional tag indicating which way the data goes, same as "inout" tag in AIDL.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Inout