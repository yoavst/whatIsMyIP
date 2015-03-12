package com.yoavst.whatismyip

import android.support.v7.app.ActionBarActivity
import android.support.v7.widget.Toolbar
import butterknife.bindView
import android.os.Bundle
import android.widget.TextView
import android.net.wifi.WifiManager
import com.yoavst.kotlin.wifiManager
import kotlin.properties.Delegates
import com.yoavst.kotlin.async
import com.yoavst.kotlin.mainThread
import android.widget.ProgressBar
import android.widget.ImageButton
import com.yoavst.kotlin.show
import com.yoavst.kotlin.hide
import tr.xip.errorview.ErrorView
import android.widget.LinearLayout
import android.net.ConnectivityManager
import java.net.InetAddress
import com.yoavst.kotlin.connectivityManager
import android.view.Menu
import android.view.MenuInflater
import com.yoavst.kotlin.broadcastReceiver
import android.content.IntentFilter

/**
 * Created by yoavst.
 */
public class MainActivity : ActionBarActivity() {
    val toolbar: Toolbar by bindView(R.id.toolbar)
    val externalIp: TextView by bindView(R.id.external_ip)
    val progress: ProgressBar by bindView(R.id.progress)
    val refresh: ImageButton by bindView(R.id.refresh)
    val error: ErrorView by bindView(R.id.error)
    val data: LinearLayout by bindView(R.id.data_layout)
    val connectionType: ItemView by bindView(R.id.connection_type)
    val internalIp: ItemView by bindView(R.id.local_ip)
    val hostname: ItemView by bindView(R.id.host_name)
    val gateway: ItemView by bindView(R.id.gateway)
    val dns: ItemView by bindView(R.id.dns)
    val connectivityChangeReceiver = broadcastReceiver { (context, intent) -> update() }
    val wifiManager: WifiManager by Delegates.lazy { wifiManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        refresh.setOnClickListener { update() }
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityChangeReceiver)
    }

    fun update() {
        if (Network.isConnected(this)) {
            progress.show()
            error.hide()
            data.show()
            async {
                try {
                    val externalIpAddress = Network.getExternalIpAddress()
                    val hostnameData = Network.getHostName(getString(android.R.string.unknownName))
                    mainThread {
                        try {
                            externalIp.setText(externalIpAddress)
                            connectionType.setData(getString(R.string.connection_type), connectionType(Network.getConnectionType(this)))
                            internalIp.setData(getString(R.string.internal_ip), Network.getLocalIpAddress())
                            hostname.setData(getString(R.string.hostname), hostnameData)
                            dns.setData(getString(R.string.dns_address), Network.getDnsAddress(getString(android.R.string.unknownName)))
                            progress.hide()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showNoConnection()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showNoConnection()
                }

            }
        } else showNoConnection()
    }

    fun connectionType(type: Int): String {
        return when (type) {
            ConnectivityManager.TYPE_MOBILE -> getString(R.string.mobile)
            ConnectivityManager.TYPE_WIFI -> getString(R.string.wifi)
            ConnectivityManager.TYPE_BLUETOOTH -> getString(R.string.bluetooth)
            ConnectivityManager.TYPE_ETHERNET -> getString(R.string.ethernet)
            ConnectivityManager.TYPE_WIMAX -> getString(R.string.wimax)
            else -> throw IllegalArgumentException()
        }
    }

    fun showNoConnection() {
        error.show()
        data.hide()
        externalIp.setText("")
        progress.hide()

    }

}