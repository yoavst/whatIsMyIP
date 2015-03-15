package com.yoavst.whatismyip

import java.net.NetworkInterface
import java.net.Inet4Address
import java.net.SocketException
import com.github.kevinsawicki.http.HttpRequest
import android.content.Context
import com.yoavst.kotlin.connectivityManager
import android.os.Build
import java.util.ArrayList

/**
 * Created by yoavst.
 */
public object Network {
    public fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress() && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        throw IllegalArgumentException()
    }

    public fun getHostName(defValue: String): String {
        try {
            val getString = javaClass<Build>().getDeclaredMethod("getString", javaClass<String>())
            getString.setAccessible(true)
            return getString.invoke(null, "net.hostname").toString()
        } catch (ex: Exception) {
            return defValue
        }
    }

    public fun getExternalIpAddress(): String? {
        return HttpRequest.get("http://api.ipify.org/").connectTimeout(5000).body()
    }

    public fun isConnected(context: Context): Boolean {
        val connectivityManager = context.connectivityManager()
        val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
        return activeNetworkInfo != null && activeNetworkInfo.isConnected()
    }

    public fun getConnectionType(context: Context): Int = context.connectivityManager().getActiveNetworkInfo().getType()

    public fun getDnsAddress(defValue: String): String {
        try {
            val SystemProperties = Class.forName("android.os.SystemProperties")
            val method = SystemProperties.getMethod("get", *array<Class<*>>(javaClass<String>()))
            val servers = ArrayList<String>()
            for (name in array("net.dns1", "net.dns2", "net.dns3", "net.dns4")) {
                val value = method.invoke(null, name) as String?
                if (value != null && "" != value && !servers.contains(value))
                   return value
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defValue
    }

}