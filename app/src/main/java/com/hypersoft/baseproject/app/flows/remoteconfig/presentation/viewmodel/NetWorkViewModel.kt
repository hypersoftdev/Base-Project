package com.hypersoft.baseproject.app.flows.remoteconfig.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hypersoft.baseproject.app.flows.remoteconfig.domain.usecase.NetworkUseCase

class NetworkViewModel(private val useCaseNetwork: NetworkUseCase) : ViewModel() {

    private val _remoteConfigStatus = MutableLiveData<Boolean>()
    val remoteConfigStatus: LiveData<Boolean> get() = _remoteConfigStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {

        startListening()
    }

    private fun startListening() {
        useCaseNetwork.startListening(
            onSuccess = {
                _remoteConfigStatus.postValue(true)
                Log.i("REMOTE_CONFIG", "Success in ViewModel ")
            },
            onFailure = { message->
                message?.let {
                    _remoteConfigStatus.postValue(false)
                    _errorMessage.postValue("Failed to fetch remote configuration")
                    Log.i("REMOTE_CONFIG", "Failure in ViewModel ")
                }?:run {
                    Log.e("REMOTE_CONFIG", "${message}")

                }

            }
        )
    }

}