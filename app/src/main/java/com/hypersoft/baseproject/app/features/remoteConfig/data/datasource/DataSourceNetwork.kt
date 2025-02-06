package com.hypersoft.baseproject.app.features.remoteConfig.data.datasource

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class DataSourceNetwork(context: Context) {

    private val connectivityManager by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    private var networkStateCallback: ((Boolean) -> Unit)? = null

    /**
     *  Public methods
     *   @see startListening
     *   @see stopListening
     */

    fun startListening(networkStateCallback: (isConnected: Boolean) -> Unit) {
        this.networkStateCallback = networkStateCallback

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        val isConnected = connectivityManager.activeNetwork != null
        networkStateCallback.invoke(isConnected)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = checkInternetAccessAndUpdateStatus()
        override fun onLost(network: Network) = updateNetworkStatus(false)
        override fun onUnavailable() = updateNetworkStatus(false)
    }

    private fun checkInternetAccessAndUpdateStatus() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val hasInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        updateNetworkStatus(hasInternet)
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        networkStateCallback?.invoke(isConnected)
    }
}