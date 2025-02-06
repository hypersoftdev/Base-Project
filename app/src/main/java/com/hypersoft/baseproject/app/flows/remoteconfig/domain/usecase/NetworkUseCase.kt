package com.hypersoft.baseproject.app.flows.remoteconfig.domain.usecase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.hypersoft.baseproject.app.flows.remoteconfig.data.datasource.NetworkStatusDataSource
import com.hypersoft.baseproject.app.flows.remoteconfig.data.repository.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetworkUseCase(private val networkStatusDataSource: NetworkStatusDataSource, private val repository: NetworkRepository) {

    private val TAG = "REMOTE_CONFIG"
    private val networkStatusLiveData: LiveData<Boolean> = networkStatusDataSource.networkStatusLiveData
    private var isFetching = false
    private var isNetworkObserverOn = false

    fun startListening(onSuccess: () -> Unit, onFailure: (message:String?) -> Unit) {
        if(isNetworkObserverOn.not())
        {
            isNetworkObserverOn=true
            val networkObserver = Observer<Boolean> { isConnected ->
                if (isConnected && isFetching.not()) {
                    Log.i(TAG, "Internet Available")
                    fetchRemoteConfig(onSuccess, onFailure)
                } else {
                    Log.i(TAG, "Internet Not Available or Already Fetching")
                }
            }

            networkStatusDataSource.startListening()
            networkStatusLiveData.observeForever(networkObserver)
            Log.i(TAG, "Listener Start")
        }else
        {
            onFailure.invoke("One listener is already attached. cannot add more listeners")
            Log.i(TAG, "Listener Already Attached")

        }
    }

    fun startListening() {
        if(isNetworkObserverOn.not())
        {
            isNetworkObserverOn=true
            val networkObserver = Observer<Boolean> { isConnected ->
                if (isConnected && isFetching.not()) {
                    Log.i(TAG, "Internet Available")
                    fetchRemoteConfig()
                } else {
                    Log.i(TAG, "Internet Not Available or Already Fetching")
                }
            }

            networkStatusDataSource.startListening()
            networkStatusLiveData.observeForever(networkObserver)
            Log.i(TAG, "Listener Start")
        }else
        {
            Log.i(TAG, "Listener Already Attached Start")

        }
    }


    fun stopListening() {
        if(isNetworkObserverOn)
        {
            networkStatusDataSource.stopListening()
            isNetworkObserverOn=false
        }else
        {
            Log.i(TAG, "No Listener is attached")

        }
    }

    private fun fetchRemoteConfig(onSuccess: () -> Unit, onFailure: (message: String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                isFetching = true
                repository.fetchRemoteConfiguration(
                    onSuccess = {
                        Log.i(TAG, "Remote Config Success")
                        onSuccess.invoke()
                        stopListening()
                    },
                    onFailure = {
                        isFetching = false
                        Log.i(TAG, "Remote Config Failed")
                        onFailure.invoke(null)
                    }
                )
            } catch (e: Exception) {
                isFetching = false
                Log.e(TAG, "Error fetching remote config: ${e.message}")
                onFailure.invoke(null)
            }
        }
    }

    private fun fetchRemoteConfig() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                isFetching = true
                repository.fetchRemoteConfiguration(
                    onSuccess = {
                        Log.i(TAG, "Remote Config Success")
                        stopListening()
                    },
                    onFailure = {
                        isFetching = false
                        Log.i(TAG, "Remote Config Failed")
                    }
                )
            } catch (e: Exception) {
                isFetching = false
                Log.e(TAG, "Error fetching remote config: ${e.message}")
            }
        }
    }
}