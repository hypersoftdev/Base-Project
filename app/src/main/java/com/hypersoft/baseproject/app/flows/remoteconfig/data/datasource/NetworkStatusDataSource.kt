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
            checkInternetAccessAndUpdateStatus()
        }

        override fun onLost(network: Network) {
            updateNetworkStatus(false)
        }

        override fun onUnavailable() {
            updateNetworkStatus(false)
        }
    }

    private fun updateNetworkStatus(isConnected: Boolean) {
        _networkStatusLiveData.postValue(isConnected)
    }


    private fun checkInternetAccessAndUpdateStatus() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val hasInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        updateNetworkStatus(hasInternet)
    }

    fun startListening() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        val isConnected = connectivityManager.activeNetwork != null
        _networkStatusLiveData.postValue(isConnected)
    }

    fun stopListening() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
