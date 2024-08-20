package com.hypersoft.baseproject.app.features.history.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hypersoft.baseproject.app.features.history.domain.entities.ItemHistory
import com.hypersoft.baseproject.app.features.history.domain.usecases.UseCaseHistory

class ViewModelHistory(private val useCaseHistory: UseCaseHistory) : ViewModel() {

    private val _historyLiveData = MutableLiveData<List<ItemHistory>>()
    val historyLiveData: LiveData<List<ItemHistory>> get() = _historyLiveData

    init {
        fetchHistory()
    }

    private fun fetchHistory() {
        _historyLiveData.value = useCaseHistory.fetchHistory()
    }
}