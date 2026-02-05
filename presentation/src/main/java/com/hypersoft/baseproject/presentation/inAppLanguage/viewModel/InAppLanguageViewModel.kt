package com.hypersoft.baseproject.presentation.inAppLanguage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.data.repositories.language.LanguageRepository
import com.hypersoft.baseproject.presentation.inAppLanguage.effect.InAppLanguageEffect
import com.hypersoft.baseproject.presentation.inAppLanguage.intent.InAppLanguageIntent
import com.hypersoft.baseproject.presentation.inAppLanguage.state.InAppLanguageState
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

class InAppLanguageViewModel(
    private val repository: LanguageRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _state = MutableStateFlow(InAppLanguageState())
    val state: StateFlow<InAppLanguageState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<InAppLanguageEffect>()
    val effect: SharedFlow<InAppLanguageEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    init {
        handleIntent(InAppLanguageIntent.LoadLanguages)
    }

    fun handleIntent(intent: InAppLanguageIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is InAppLanguageIntent.LoadLanguages -> loadLanguages()
            is InAppLanguageIntent.NavigateBack -> _effect.emit(InAppLanguageEffect.NavigateBack)
            is InAppLanguageIntent.SelectLanguage -> selectLanguage(intent.languageCode)
            is InAppLanguageIntent.ApplyLanguage -> applyLanguage()
        }
    }

    private suspend fun loadLanguages() {
        _state.update { it.copy(isLoading = true) }

        repository
            .getLanguages()
            .map { list ->
                val selectedCode = list.find { it.isSelected }?.languageCode
                _state.update { it.copy(selectedLanguageCode = selectedCode) }

                list.map { language ->
                    language.copy(
                        isSelected = language.languageCode == selectedCode,
                        itemClick = { handleIntent(InAppLanguageIntent.SelectLanguage(language.languageCode)) }
                    )
                }
            }
            .flowOn(defaultDispatcher)
            .catch {
                handleError(it)
            }
            .collect { list ->
                _state.update { it.copy(isLoading = false, languages = list) }
            }
    }

    private fun selectLanguage(selectedCode: String) {
        _state.update { it.copy(selectedLanguageCode = selectedCode) }

        val updatedList = _state.value.languages.map { it.copy(isSelected = it.languageCode == selectedCode) }

        _state.update { it.copy(languages = updatedList) }
    }

    private suspend fun applyLanguage() {
        state.value.selectedLanguageCode?.let {
            repository.applyLanguage(it)
            _effect.emit(InAppLanguageEffect.NavigateBack)
        } ?: run {
            handleError(NullPointerException("Something went wrong, try again later"))
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(InAppLanguageEffect.ShowError(errorMessage))
    }
}