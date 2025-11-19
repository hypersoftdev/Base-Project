package com.hypersoft.baseproject.presentation.language.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.data.repositories.repositories.LanguageRepository
import com.hypersoft.baseproject.presentation.language.effect.LanguageEffect
import com.hypersoft.baseproject.presentation.language.intent.LanguageIntent
import com.hypersoft.baseproject.presentation.language.state.LanguageState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val repository: LanguageRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _state = MutableStateFlow(LanguageState())
    val state: StateFlow<LanguageState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LanguageEffect>()
    val effect: SharedFlow<LanguageEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    fun handleIntent(intent: LanguageIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is LanguageIntent.LoadLanguages -> loadLanguages()
            is LanguageIntent.SelectLanguage -> selectLanguage(intent.languageCode)
            is LanguageIntent.ApplyLanguage -> applyLanguage()
            is LanguageIntent.ErrorCleared -> _state.update { it.copy(error = null) }
        }
    }

    private suspend fun loadLanguages() {
        _state.update { it.copy(isLoading = true, error = null) }

        repository
            .getLanguages()
            .map { languages ->
                languages.map { language ->
                    if (language.isSelected) {
                        _state.update { it.copy(selectedLanguageCode = language.languageCode) }
                    }

                    language.copy(itemClick = { selectLanguage(language.languageCode) })
                }
            }
            .flowOn(defaultDispatcher)
            .catch {
                handleError(it)
            }
            .collect { languages ->
                _state.update { it.copy(isLoading = false, languages = languages, error = null) }
            }
    }

    private fun selectLanguage(selectedCode: String) = viewModelScope.launch {
        _state.update { it.copy(selectedLanguageCode = selectedCode) }

        repository
            .updateLanguageCode(selectedCode)
            .collect { languages ->
                _state.update { it.copy(languages = languages, error = null) }
            }
    }

    private suspend fun applyLanguage() {
        state.value.selectedLanguageCode?.let {
            repository.applyLanguage(it)
            _effect.emit(LanguageEffect.NavigateToDashboard)
        } ?: run {
            handleError(NullPointerException("Something went wrong, try again later"))
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(LanguageEffect.ShowError(errorMessage))
    }
}