package com.hypersoft.baseproject.app.features.remoteConfig.data.repositories

import com.hypersoft.baseproject.app.features.remoteConfig.data.datasource.DataSourceNetwork
import com.hypersoft.baseproject.app.features.remoteConfig.domain.repositories.RepositoryNetwork

class RepositoryNetworkImpl(private val dataSourceNetwork: DataSourceNetwork) : RepositoryNetwork {

    override fun startListeningNetworkState(networkStateCallback: (isConnected: Boolean) -> Unit) {
        dataSourceNetwork.startListeningNetworkState(networkStateCallback)
    }

    override fun stopListeningNetworkState() {
        dataSourceNetwork.stopListening()
    }
}