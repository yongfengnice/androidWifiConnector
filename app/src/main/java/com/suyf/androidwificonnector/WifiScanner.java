package com.suyf.androidwificonnector;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

/**
 * @author：yongfeng
 * @data：2024/2/26 18:41
 */
public class WifiScanner {

    private static final String TAG = "WifiScanner";

    WifiScanReceiver wifiScanReceiver = new WifiScanReceiver();
    private static ScanResultCallback scanResultCallback;

    private Context mContext;

    WifiScanner(Context context){
        this.mContext = context.getApplicationContext();
    }

    public void setScanResultCallback(ScanResultCallback scanResultCallback) {
        this.scanResultCallback = scanResultCallback;
    }

    public void scanWifi() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        // 检查WiFi是否开启
        if (!wifiManager.isWifiEnabled()) {
            Log.d(TAG, "WiFi is disabled. Enabling WiFi...");
            wifiManager.setWifiEnabled(true);
        }
        // 注册一个广播接收器来接收扫描结果
        mContext.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // 开始扫描WiFi
        wifiManager.startScan();
    }

    public void stopScanWifi() {
        try {
            mContext.unregisterReceiver(wifiScanReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static class WifiScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResultCallback != null) {
                    scanResultCallback.onScanResult(scanResults);
                }
                for (ScanResult scanResult : scanResults) {
                    Log.d(TAG, "SSID: " + scanResult.SSID + ", BSSID: " + scanResult.BSSID);
                }
            }
        }
    }

    public interface ScanResultCallback {
        void onScanResult(List<ScanResult> scanResultList);
    }
}
