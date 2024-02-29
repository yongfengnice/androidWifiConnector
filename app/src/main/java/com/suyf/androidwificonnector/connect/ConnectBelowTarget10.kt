package com.suyf.androidwificonnector.connect

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager

/**
 * @author：yongfeng
 * @data：2024/2/28 16:54
 */
class ConnectBelowTarget10(private val context: Context, scanResult: ScanResult, password: String) :
    AbstractConnect(scanResult, password) {

    private var connectManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var wifiManager: WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override fun connect() {
        if (!isWifiEnable()) {
            wifiManager.isWifiEnabled = true
        }
        val info: WifiInfo = wifiManager.connectionInfo
        if (info != null && WifiUtils.isWifi(context) && info.ssid != null) {
            val ssidEquals = WifiUtils.isSsidEquals(scanResult.SSID, info.ssid, true)
            if (ssidEquals) {
                //已经连接上了，直接返回
                return
            }
        }
        realConnect()
    }

    private fun realConnect() {
        val type = WifiUtils.getType(scanResult)
        var config: WifiConfiguration? = null
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        if (wifiInfo != null) {
            wifiManager.disableNetwork(wifiInfo.networkId)
            wifiManager.disconnect()
        }

        config = wifiManager.getConfiguredNetworks()?.find { it.SSID == scanResult.SSID }
        if (config == null) {
            config = WifiConfiguration()
        }

        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        config.SSID = scanResult.SSID
        when (type) {
            0 -> {
                config.allowedKeyManagement.set(0)
            }
            1 -> {
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + password + "\""
                config.allowedAuthAlgorithms.set(1)
                config.allowedGroupCiphers.set(3)
                config.allowedGroupCiphers.set(2)
                config.allowedGroupCiphers.set(0)
                config.allowedGroupCiphers.set(1)
                config.allowedKeyManagement.set(0)
                config.wepTxKeyIndex = 0
            }
            2 -> {
                config.preSharedKey = "\"" + password + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(0)
                config.allowedGroupCiphers.set(2)
                config.allowedKeyManagement.set(1)
                config.allowedPairwiseCiphers.set(1)
                config.allowedGroupCiphers.set(3)
                config.allowedPairwiseCiphers.set(2)
                config.status = 2
            }
        }

        var netId = config.networkId
        if (netId == -1) {
            netId = wifiManager.addNetwork(config)
        }
        wifiManager.enableNetwork(netId, true)
    }

    override fun disconnect() {
    }

    override fun isWifiEnable(): Boolean {
        return wifiManager.isWifiEnabled
    }

    override fun destroy() {
        disconnect()
    }
}