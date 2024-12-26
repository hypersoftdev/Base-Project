package com.hypersoft.baseproject.app.flows.remoteconfig.data.datasource

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NetworkStatusDataSource(private val context: Context) {

    private val _networkStatusLiveData = MutableLiveData<Boolean>()
    val networkStatusLiveData: LiveData<Boolean> = _networkStatusLiveData

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetworkStatus(true) // Network is available
        }

        override fun onLost(network: Network) {
            updateNetworkStatus(false) // Network is lost
        }

        override fun onUnavailable() {
            updateNetworkStatus(false) // No network available
        }
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        _networkStatusLiveData.postValue(isConnected)
    }

    fun startListening() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Initial state check
        val isConnected = connectivityManager.activeNetwork != null
        _networkStatusLiveData.postValue(isConnected)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
