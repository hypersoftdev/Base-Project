package com.hypersoft.baseproject.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.hypersoft.baseproject.core.firebase.FirebaseUtils.recordException

class InternetManager(context: Context) {

    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    val isInternetConnected: Boolean
        get() {
            try {
                val network = connectivityManager.activeNetwork ?: return false
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            } catch (ex: Exception) {
                ex.recordException("Internet Manager")
                return false
            }
        }
}