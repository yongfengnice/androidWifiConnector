package com.suyf.androidwificonnector.connect

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * @author：yongfeng
 * @data：2024/2/28 16:54
 */
@RequiresApi(Build.VERSION_CODES.Q)
class ConnectAboveTarget10(private val context: Context, scanResult: ScanResult, password: String) :
    AbstractConnect(scanResult, password) {

    private var connectManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var wifiManager: WifiManager =
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connectManager.bindProcessToNetwork(network)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            connectManager.bindProcessToNetwork(null)
        }
    }

    override fun isWifiEnable(): Boolean {
        return wifiManager.isWifiEnabled
    }

    override fun destroy() {
        disconnect()
    }

    override fun connect() {
        Log.d(TAG, "connect: ssid=${scanResult.SSID},password=${password}")

        val networkSuggestion =
            WifiNetworkSuggestion.Builder().setSsid(scanResult.SSID).setWpa2Passphrase(password)
                .setIsAppInteractionRequired(false).build()
        wifiManager.addNetworkSuggestions(listOf(networkSuggestion))

        val specifierBuilder = WifiNetworkSpecifier.Builder()
        specifierBuilder.setSsid(scanResult.SSID).setWpa2Passphrase(password)
        val networkRequest =
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifierBuilder.build()).build()
        Log.d(TAG, "connect:requestNetwork..........${scanResult.SSID}")
        disconnect()
        connectManager.requestNetwork(networkRequest, networkCallback)
    }

    override fun disconnect() {
        try {
            connectManager.unregisterNetworkCallback(this.networkCallback)
            connectManager.bindProcessToNetwork(null)
        } catch (e: Exception) {

        }
    }
}