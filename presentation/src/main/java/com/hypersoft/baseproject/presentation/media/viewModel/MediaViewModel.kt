package com.hypersoft.baseproject.presentation.media.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.media.effect.MediaEffect
import com.hypersoft.baseproject.presentation.media.intent.MediaIntent
import com.hypersoft.baseproject.presentation.media.state.MediaState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaViewModel : ViewModel() {

    private val _state = MutableStateFlow(MediaState())
    val state: StateFlow<MediaState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaEffect>()
    val effect: SharedFlow<MediaEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    fun handleIntent(intent: MediaIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaIntent.NavigateBack -> _effect.emit(MediaEffect.NavigateBack)
            is MediaIntent.NavigateToImages -> _effect.emit(MediaEffect.NavigateToImages)
            is MediaIntent.NavigateToVideos -> _effect.emit(MediaEffect.NavigateToVideos)
            is MediaIntent.NavigateToAudios -> _effect.emit(MediaEffect.NavigateToAudios)
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(error = errorMessage) }
        _effect.emit(MediaEffect.ShowError(errorMessage))
    }
}