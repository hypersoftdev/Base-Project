package com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase

import android.util.Log
import com.hypersoft.baseproject.app.features.remoteConfig.domain.repositories.RepositoryRemoteConfig
import com.hypersoft.baseproject.utilities.utils.ConstantUtils.TAG

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class UseCaseRemoteConfig(private val repositoryRemoteConfig: RepositoryRemoteConfig) {

    private var isFetching = false

    fun fetchRemoteConfig(isInternetConnected: Boolean, responseCallback: (fetchSuccessfully: Boolean, errorMessage: String?) -> Unit) {
        if (isInternetConnected.not()) {
            Log.e(TAG, "fetchRemoteConfig: No Internet available yet!")
            return
        }

        isFetching = true
        repositoryRemoteConfig.fetchRemoteConfiguration { isFetched: Boolean, errorMessage: String? ->
            Log.d(TAG, "fetchRemoteConfig: isFetched: $isFetched, Error Message: $errorMessage")
            isFetching = false
            responseCallback.invoke(isFetched, errorMessage)
        }
    }
}