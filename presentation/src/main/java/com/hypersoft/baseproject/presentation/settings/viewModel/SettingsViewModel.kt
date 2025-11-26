package com.hypersoft.baseproject.presentation.settings.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.settings.effect.SettingsEffect
import com.hypersoft.baseproject.presentation.settings.intent.SettingsIntent
import com.hypersoft.baseproject.presentation.settings.state.SettingsState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SettingsEffect>()
    val effect: SharedFlow<SettingsEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    fun handleIntent(intent: SettingsIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        _state.update { it.copy(error = null) }

        when (intent) {
            is SettingsIntent.LoadVersionName -> loadVersionName(intent.versionName)
            is SettingsIntent.LanguageClicked -> _effect.emit(SettingsEffect.NavigateToLanguage)
            is SettingsIntent.FeedbackClicked -> _effect.emit(SettingsEffect.GiveFeedback)
            is SettingsIntent.RateUsClicked -> _effect.emit(SettingsEffect.ShowRateUsDialog)
            is SettingsIntent.ShareAppClicked -> _effect.emit(SettingsEffect.ShareApp)
            is SettingsIntent.PrivacyPolicyClicked -> _effect.emit(SettingsEffect.OpenPrivacyPolicy)
        }
    }

    private fun loadVersionName(versionName: String) {
        _state.update { it.copy(versionName = versionName) }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"

        _state.update { it.copy(error = errorMessage) }
        _effect.emit(SettingsEffect.ShowError(errorMessage))
    }
}