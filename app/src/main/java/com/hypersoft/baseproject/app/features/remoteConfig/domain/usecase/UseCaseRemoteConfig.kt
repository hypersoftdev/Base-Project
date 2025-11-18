package com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase

import android.util.Log
import com.hypersoft.baseproject.app.features.remoteConfig.data.repositories.RepositoryRemoteConfigImpl
import com.hypersoft.baseproject.utilities.utils.ConstantUtils.TAG

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

class UseCaseRemoteConfig(private val repositoryRemoteConfigImpl: RepositoryRemoteConfigImpl) {

    private var isFetching = false

    fun fetchRemoteConfig(isInternetConnected: Boolean, responseCallback: (fetchSuccessfully: Boolean, errorMessage: String?) -> Unit) {
        if (isInternetConnected.not()) {
            Log.e(TAG, "UseCaseRemoteConfig: fetchRemoteConfig: No Internet available yet!")
            return
        }

        isFetching = true
        Log.d(TAG, "fetchRemoteConfig: fetching")
        repositoryRemoteConfigImpl.fetchRemoteConfiguration { isFetched: Boolean, errorMessage: String? ->
            Log.d(TAG, "UseCaseRemoteConfig: fetchRemoteConfig: isFetched: $isFetched, Error Message: $errorMessage")
            isFetching = false
            responseCallback.invoke(isFetched, errorMessage)
        }
    }
}