package com.ibroadlink.library.base.callback.eventbus

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

internal data class StickyData(
    val liveData: MutableLiveData<Any>,
    var registrants: Any?,
    var observer: Observer<Any>?,
    var isValueValid: Boolean
)