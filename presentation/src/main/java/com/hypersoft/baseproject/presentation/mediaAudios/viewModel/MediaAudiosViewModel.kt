package com.hypersoft.baseproject.presentation.mediaAudios.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.domain.media.useCases.contentObserver.ObserveMediaChangesUseCase
import com.hypersoft.baseproject.presentation.mediaAudios.effect.MediaAudiosEffect
import com.hypersoft.baseproject.presentation.mediaAudios.intent.MediaAudiosIntent
import com.hypersoft.baseproject.presentation.mediaAudios.state.MediaAudiosState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaAudiosViewModel(
    private val getAudiosUseCase: GetAudiosUseCase,
    private val observeMediaChangesUseCase: ObserveMediaChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaAudiosState())
    val state: StateFlow<MediaAudiosState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaAudiosEffect>()
    val effect: SharedFlow<MediaAudiosEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private var observeJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeMediaStore()
        handleIntent(MediaAudiosIntent.LoadAudios)
    }

    fun handleIntent(intent: MediaAudiosIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaAudiosIntent.LoadAudios -> loadAudios()
            is MediaAudiosIntent.AudioClicked -> _effect.emit(MediaAudiosEffect.NavigateToDetail(intent.audioUri))
        }
    }

    private fun observeMediaStore() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeMediaChangesUseCase
                .observeAudios()
                .collectLatest {
                    handleIntent(MediaAudiosIntent.LoadAudios)
                }
        }
    }

    private fun loadAudios() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(coroutineExceptionHandler) {
            _state.update { it.copy(isLoading = true) }

            val audios = getAudiosUseCase()
            _state.update {
                it.copy(isLoading = false, audios = audios)
            }
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(MediaAudiosEffect.ShowError(errorMessage))
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
        loadJob?.cancel()
    }
}