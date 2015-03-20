package com.yoavst.whatismyip

import android.content.Context
import android.os.Build
import com.github.kevinsawicki.http.HttpRequest
import com.yoavst.kotlin.connectivityManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.ArrayList

/**
 * Created by yoavst.
 */
public object Network {
    /**
     * Returns the local ip address
     *
     * @return The local ip address
     */
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

    /**
     * Returns the host name
     * @param defValue Default value if host name not found
     * @return the host name or [defValue]
     */
    public fun getHostName(defValue: String): String {
        try {
            val getString = javaClass<Build>().getDeclaredMethod("getString", javaClass<String>())
            getString.setAccessible(true)
            return getString.invoke(null, "net.hostname").toString()
        } catch (ex: Exception) {
            return defValue
        }
    }

    /**
     * Returns external IP address
     * @return External IP address
     */
    public fun getExternalIpAddress(): String? {
        return HttpRequest.get("http://api.ipify.org/").connectTimeout(5000).body()
    }

    /**
     * Returns true if connected to network
     * @param context Context for getting [ConnectivityManager][android.net.ConnectivityManager]
     * @return true if connected to network
     */
    public fun isConnected(context: Context): Boolean {
        val connectivityManager = context.connectivityManager()
        val activeNetworkInfo = connectivityManager.getActiveNetworkInfo()
        return activeNetworkInfo != null && activeNetworkInfo.isConnected()
    }

    /**
     * Returns the connection type
     * @param context Context for getting [ConnectivityManager][android.net.ConnectivityManager]
     * @return the connection type
     */
    public fun getConnectionType(context: Context): Int = context.connectivityManager().getActiveNetworkInfo().getType()

    /**
     * Returns the DNS address
     * @param defValue Default value if host name not found
     * @return the DNS address or [defValue]
     */
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