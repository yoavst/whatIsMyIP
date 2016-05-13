package com.yoavst.whatismyip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*

/**
 * Created by yoavst.
 */
class MainActivity : AppCompatActivity() {
    val connectivityChangeReceiver = broadcastReceiver { context, intent -> update() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        refresh.setOnClickListener { update() }
        update()
    }

    fun broadcastReceiver(init: (Context, Intent?) -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                init(context, intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_about) {
            alert(R.string.about_text, R.string.about).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(connectivityChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(connectivityChangeReceiver)
    }

    /**
     * Update info and screen
     */
    fun update() {
        if (Network.isConnected(this)) {
            progress.show()
            error.hide()
            async() {
                try {
                    val externalIpAddress = Network.getExternalIpAddress()
                    val hostnameData = Network.getHostName(getString(android.R.string.unknownName))
                    uiThread {
                        try {
                            externalIp.text = externalIpAddress
                            connectionType.setData(getString(R.string.connection_type), connectionType(Network.getConnectionType(ctx)))
                            internalIp.setData(getString(R.string.internal_ip), Network.getLocalIpAddress())
                            hostName.setData(getString(R.string.hostname), hostnameData)
                            dns.setData(getString(R.string.dns_address), Network.getDnsAddress(getString(android.R.string.unknownName)))
                            data.show()
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

    /**
     * Convert connection type to text
     * @param type A connection type
     * @return Readable connection type
     */
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

    /**
     * Shows no connection screen.
     * It make the error layout visible, and clean the titles and progress bar.
     */
    fun showNoConnection() {
        onUiThread {
            error.show()
            data.hide()
            externalIp.text = ""
            progress.hide()
        }
    }

}