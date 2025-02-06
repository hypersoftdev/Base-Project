package com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase

import com.hypersoft.baseproject.app.features.remoteConfig.data.repositories.RepositoryNetworkImpl

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class UseCaseNetwork(private val repositoryNetworkImpl: RepositoryNetworkImpl) {

    fun fetchNetworkState(networkStateCallback: (isConnected: Boolean) -> Unit) {
        repositoryNetworkImpl.fetchNetworkState(networkStateCallback)
    }
}