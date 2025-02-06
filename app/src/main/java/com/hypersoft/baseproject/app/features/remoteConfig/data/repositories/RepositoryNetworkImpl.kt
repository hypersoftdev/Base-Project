package com.hypersoft.baseproject.app.features.remoteConfig.data.repositories

import android.util.Log
import com.hypersoft.baseproject.app.features.remoteConfig.data.datasource.DataSourceNetwork
import com.hypersoft.baseproject.app.features.remoteConfig.domain.repositories.RepositoryNetwork
import com.hypersoft.baseproject.di.setup.DIComponent

class RepositoryNetworkImpl(private val dataSourceNetwork: DataSourceNetwork) : RepositoryNetwork {
    
    override fun fetchNetworkState(networkStateCallback: (isConnected: Boolean) -> Unit) {
        dataSourceNetwork.startListening(networkStateCallback)
    }


    val diComponent = DIComponent()
    override fun fetchRemoteConfiguration(attempts: Int = 3, onSuccess: () -> Unit, onFailure: () -> Unit) {

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
