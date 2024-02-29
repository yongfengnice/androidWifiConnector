package com.suyf.androidwificonnector.connect

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Build.VERSION
import android.widget.Toast

/**
 * @author：yongfeng
 * @data：2024/2/28 16:51
 */
object WifiConnector {

    private var connector: AbstractConnect? = null
    private var appContext: Context? = null

    private fun initParam(context: Context, scanResult: ScanResult, password: String): WifiConnector {
        val applicationContext = context.applicationContext
        appContext = applicationContext
        connector?.destroy()
        connector = if (VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ConnectAboveTarget10(applicationContext, scanResult, password)
        } else {
            ConnectBelowTarget10(applicationContext, scanResult, password)
        }
        return this
    }

    fun doConnect(context: Context, scanResult: ScanResult, password: String) {
        initParam(context, scanResult, password)
        if (connector?.isWifiEnable() != true) {
            appContext?.let {
                Toast.makeText(it, "请先打开Wifi功能", Toast.LENGTH_SHORT).show()
            }
            return
        }
        connector?.connect()
    }

    fun doDisconnect() {
        connector?.disconnect()
    }

}