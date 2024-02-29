package com.suyf.androidwificonnector.connect

import android.net.wifi.ScanResult

/**
 * @author：yongfeng
 * @data：2024/2/28 16:51
 */
abstract class AbstractConnect(val scanResult: ScanResult, val password: String) {
    val TAG: String = this.javaClass.simpleName

    abstract fun connect()

    abstract fun disconnect()

    abstract fun isWifiEnable(): Boolean

    abstract fun destroy()
}