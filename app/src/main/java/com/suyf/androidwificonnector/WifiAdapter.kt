package com.suyf.androidwificonnector

import android.app.AlertDialog
import android.net.wifi.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author：yongfeng
 * @data：2024/2/28 16:27
 */
class WifiAdapter(private val wifiList: List<ScanResult>) :
    RecyclerView.Adapter<WifiAdapter.WifiAdapterHolder>() {

    var itemClickCallback: ((ScanResult, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiAdapterHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wifi, parent, false)
        return WifiAdapterHolder(view)
    }

    override fun getItemCount(): Int {
        return wifiList.size
    }

    override fun onBindViewHolder(holder: WifiAdapterHolder, position: Int) {
        val ssid =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) wifiList[position].wifiSsid else wifiList[position].SSID
        holder.wifiNameTv.text = "${position + 1}:wifi名称:${ssid}"
        holder.itemView.setOnClickListener {
            val editText = EditText(holder.itemView.context);
            val alertDialogBuilder = AlertDialog.Builder(holder.itemView.context)
                .setMessage("请输入wifi密码")
                .setNegativeButton("取消") { _, _ ->

                }
                .setPositiveButton("确定") { _, _ ->
                    itemClickCallback?.invoke(wifiList[position], editText.text.toString())
                }
                .setView(editText)
            alertDialogBuilder.create().show()
        }
    }

    inner class WifiAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wifiNameTv: TextView = itemView.findViewById<TextView>(R.id.wifi_name)
    }
}