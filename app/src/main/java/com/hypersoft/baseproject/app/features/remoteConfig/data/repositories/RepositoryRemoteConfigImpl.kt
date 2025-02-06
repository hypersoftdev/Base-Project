package com.hypersoft.baseproject.app.features.remoteConfig.data.repositories

import com.hypersoft.baseproject.app.features.remoteConfig.data.datasource.DataSourceRemoteConfig
import com.hypersoft.baseproject.app.features.remoteConfig.domain.repositories.RepositoryRemoteConfig

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class RepositoryRemoteConfigImpl(private val dataSourceRemoteConfig: DataSourceRemoteConfig) : RepositoryRemoteConfig {

    override fun fetchRemoteConfiguration(responseCallback: (fetchSuccessfully: Boolean, errorMessage: String?) -> Unit) {
        dataSourceRemoteConfig.checkRemoteConfig(responseCallback)
    }
}