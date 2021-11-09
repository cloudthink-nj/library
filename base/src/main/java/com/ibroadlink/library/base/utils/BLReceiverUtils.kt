package com.ibroadlink.library.base.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

abstract class BLReceiverUtils(vararg actions: String) : BroadcastReceiver() {

    private val filter = IntentFilter()

    private var registered = false

    init {
        for (action in actions) {
            filter.addAction(action)
        }
    }

    protected abstract fun onReceiveAction(action: String, intent: Intent)

    fun getFilter() = filter

    override fun onReceive(context: Context, intent: Intent?) {
        intent ?: return
        val action = intent.action
        if (!action.isNullOrEmpty()) {
            onReceiveAction(action, intent)
        }
    }

    fun register(context: Context?) {
        if (!registered) {
            context?.registerReceiver(this, filter)
            registered = true
        }
    }

    fun unregister(context: Context?) {
        if (registered) {
            registered = false
            context?.unregisterReceiver(this)
        }
    }
}
