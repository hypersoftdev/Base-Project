package com.hypersoft.baseproject.app.flows.entrance.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModelEntrance : ViewModel() {

    private val _navigateLiveData = MutableLiveData<Boolean>()
    val navigateLiveData: LiveData<Boolean> get() = _navigateLiveData

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            delay(3000)
            _navigateLiveData.value = true
        }
    }
}