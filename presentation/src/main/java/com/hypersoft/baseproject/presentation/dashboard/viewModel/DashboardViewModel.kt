package com.hypersoft.baseproject.presentation.dashboard.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.dashboard.effect.DashboardEffect
import com.hypersoft.baseproject.presentation.dashboard.intent.DashboardIntent
import com.hypersoft.baseproject.presentation.dashboard.state.DashboardState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect: SharedFlow<DashboardEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    fun handleIntent(intent: DashboardIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is DashboardIntent.RegisterBackPress -> _effect.emit(DashboardEffect.RegisterBackPress)
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _effect.emit(DashboardEffect.ShowError(errorMessage))
    }
}