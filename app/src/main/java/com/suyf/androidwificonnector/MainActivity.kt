package com.suyf.androidwificonnector

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.suyf.androidwificonnector.connect.WifiConnector


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var mConnectivityManager: ConnectivityManager

    private lateinit var wifiRv: RecyclerView
    private lateinit var wifiAdapter: WifiAdapter
    private val wifiList = mutableListOf<ScanResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        mConnectivityManager =
            applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.CHANGE_NETWORK_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ), 0
        )
        getWifiList()
    }

    private fun initView() {
        wifiRv = findViewById(R.id.wifi_rv)
        wifiAdapter = WifiAdapter(wifiList)
        wifiAdapter.itemClickCallback = { scanResult: ScanResult, password: String ->
            WifiConnector.doConnect(this, scanResult, password)
        }
        wifiRv.adapter = wifiAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getWifiList()
    }


    private fun getWifiList() {
        val wifiScanner = WifiScanner(this)
        wifiScanner.setScanResultCallback {
            Log.d(TAG, "getWifiList: ${it.size}")
            it?.apply {
                showWifiList(this)
            }
            wifiScanner.stopScanWifi()
        }
        wifiScanner.scanWifi()
    }

    private fun showWifiList(scanResults: List<ScanResult>) {
        runOnUiThread {
            wifiList.clear()
            wifiList.addAll(scanResults)
            wifiAdapter.notifyDataSetChanged()
        }
    }

    fun disconnect(view: View) {
        WifiConnector.doDisconnect()
    }

    fun scanWifi(view: View) {
        getWifiList()
    }

}