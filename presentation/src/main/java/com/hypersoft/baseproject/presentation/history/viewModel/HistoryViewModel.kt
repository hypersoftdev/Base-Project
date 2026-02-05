package com.hypersoft.baseproject.presentation.history.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.data.repositories.history.HistoryRepository
import com.hypersoft.baseproject.presentation.history.effect.HistoryEffect
import com.hypersoft.baseproject.presentation.history.intent.HistoryIntent
import com.hypersoft.baseproject.presentation.history.state.HistoryState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HistoryEffect>()
    val effect: SharedFlow<HistoryEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    init {
        handleIntent(HistoryIntent.LoadHistories)
    }

    fun handleIntent(intent: HistoryIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is HistoryIntent.LoadHistories -> loadHistories()
        }
    }

    private suspend fun loadHistories() {
        _state.update { it.copy(isLoading = true) }
        val histories = repository.getHistories()
        _state.update { it.copy(histories = histories, isLoading = false) }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(HistoryEffect.ShowError(errorMessage))
    }
}