package com.example.projekconsultant.methodsNService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class NetworkChangeReceiver(private val callback: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val isConnected = NetworkUtils.isInternetAvailable(context)
        callback(isConnected)
    }
}


