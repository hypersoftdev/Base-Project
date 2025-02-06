package com.hypersoft.baseproject.app.flows.remoteconfig.data.repository

import android.util.Log
import com.hypersoft.baseproject.di.setup.DIComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class NetworkRepository {

    private val TAG = "REMOTE_CONFIG"

    val diComponent = DIComponent()
    fun fetchRemoteConfiguration(
        attempts: Int = 3,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        CoroutineScope(IO).launch {
            Log.i(TAG, "Fetching Remote Config")
            diComponent.remoteConfiguration.checkRemoteConfig { fetchSuccessfully ->
                if (fetchSuccessfully) {
                    onSuccess.invoke()
                } else {
                    if (attempts > 1) {
                        Log.i("REMOTE_CONFIG", "Retrying... Remaining attempts: ${attempts - 1}")
                        fetchRemoteConfiguration(attempts - 1, onSuccess, onFailure)
                    } else {
                        Log.e("REMOTE_CONFIG", "All retries failed or Internet Disconnected")
                        onFailure.invoke()
                    }
                }
            }
        }
    }
}
