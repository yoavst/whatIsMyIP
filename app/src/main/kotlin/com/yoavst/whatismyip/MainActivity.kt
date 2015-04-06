package com.yoavst.whatismyip

import android.app.AlertDialog
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.yoavst.kotlin.*
import kotlinx.android.synthetic.activity_main.*
/**
 * Created by yoavst.
 */
public class MainActivity : ActionBarActivity() {
    val connectivityChangeReceiver = broadcastReceiver { context, intent -> update() }
    val connectivity: ConnectivityManager by systemService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        e(connectivity.getActiveNetworkInfo().getTypeName())
        refresh.setOnClickListener { update() }
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.getItemId() == R.id.action_about) {
            val builder = if (beforeLollipop()) AlertDialog.Builder(this) else AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
            builder.setTitle(R.string.about).setMessage(R.string.about_text).show()
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
            async {
                try {
                    val externalIpAddress = Network.getExternalIpAddress()
                    val hostnameData = Network.getHostName(getString(android.R.string.unknownName))
                    mainThread {
                        try {
                            externalIp.setText(externalIpAddress)
                            connectionType.setData(getString(R.string.connection_type), connectionType(Network.getConnectionType(this)))
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
        error.show()
        data.hide()
        externalIp.setText("")
        progress.hide()

    }

}