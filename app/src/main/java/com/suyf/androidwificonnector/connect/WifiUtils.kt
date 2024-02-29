package com.suyf.androidwificonnector.connect

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.text.TextUtils

/**
 * @author：yongfeng
 * @data：2024/2/28 17:37
 */
object WifiUtils {

    //是否处于wifi连接
    fun isWifi(mContext: Context): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo != null && activeNetInfo.type == 1
    }

    //ssid是否相同
    fun isSsidEquals(ssid: String, SSID: String, bySsidIgnoreCase: Boolean): Boolean {
        return if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(SSID)) {
            var ssidEquals =
                if (bySsidIgnoreCase) ssid.equals(SSID, ignoreCase = true) else ssid == SSID
            if (!ssidEquals) {
                val quotedString = convertToQuotedString(ssid)
                ssidEquals = if (bySsidIgnoreCase) quotedString.equals(
                    SSID,
                    ignoreCase = true
                ) else quotedString == SSID
            }
            ssidEquals
        } else {
            false
        }
    }

    fun getType(scanResult: ScanResult): Int {
        return if (scanResult.capabilities.contains("WPA")) {
            2
        } else if (scanResult.capabilities.contains("WEP")) {
            1
        } else {
            0
        }
    }

    fun convertToQuotedString(string: String): String? {
        return if (TextUtils.isEmpty(string)) {
            ""
        } else {
            val lastPos = string.length - 1
            if (lastPos >= 0 && (string[0] != '"' || string[lastPos] != '"')) "\"" + string + "\"" else string
        }
    }
}