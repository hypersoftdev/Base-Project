package com.hypersoft.baseproject.app.features.remoteConfig.domain.repositories

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

interface RepositoryNetwork {
    fun fetchNetworkState(networkStateCallback: (isConnected: Boolean) -> Unit)
}