package com.hypersoft.baseproject.presentation.entrance.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.data.repositories.entrance.EntranceRepository
import com.hypersoft.baseproject.presentation.entrance.effect.EntranceEffect
import com.hypersoft.baseproject.presentation.entrance.intent.EntranceIntent
import com.hypersoft.baseproject.presentation.entrance.state.EntranceState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EntranceViewModel(private val entranceRepository: EntranceRepository) : ViewModel() {

    private val _state = MutableStateFlow(EntranceState())
    val state: StateFlow<EntranceState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EntranceEffect>()
    val effect: SharedFlow<EntranceEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }

    init {
        handleIntent(EntranceIntent.Initialize)
    }

    fun handleIntent(intent: EntranceIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is EntranceIntent.Initialize -> initializeScreen()
        }
    }

    private suspend fun initializeScreen() {
        _state.update { it.copy(isLoading = true) }

        delay(SPLASH_DELAY_MS)

        _state.update { it.copy(isLoading = false) }

        when (entranceRepository.isFirstTime()) {
            true -> _effect.emit(EntranceEffect.NavigateToLanguage)
            false -> _effect.emit(EntranceEffect.NavigateToDashboard)
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(EntranceEffect.ShowError(errorMessage))
    }
}