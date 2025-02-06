package com.hypersoft.baseproject.app.features.remoteConfig.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase.UseCaseNetwork
import com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase.UseCaseRemoteConfig

class NetworkViewModel(private val useCaseNetwork: UseCaseNetwork, private val useCaseRemoteConfig: UseCaseRemoteConfig) : ViewModel() {

    private val _remoteSuccessLiveData = MutableLiveData<Unit>()
    val remoteSuccessLiveData: LiveData<Unit> get() = _remoteSuccessLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    init {
        fetchNetworkState()
    }

    private fun fetchNetworkState() {
        useCaseNetwork.fetchNetworkState { fetchRemoteConfig(it) }
    }

    private fun fetchRemoteConfig(isInternetConnected: Boolean) {
        useCaseRemoteConfig.fetchRemoteConfig(
            isInternetConnected = isInternetConnected,
            responseCallback = { isFetched: Boolean, errorMessage: String? ->
                when (isFetched) {
                    true -> _remoteSuccessLiveData.postValue(Unit)
                    false -> _errorLiveData.postValue("Failed to fetch Remote Configurations: Error Message: $errorMessage")
                }
            }
        )
    }
}