package com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for audio playback screen.
 * Follows MVI pattern:
 * - Handles intents from UI
 * - Updates state based on player events
 * - Emits effects for navigation/errors
 */
class MediaAudioDetailViewModel(private val getAudiosUseCase: GetAudiosUseCase) : ViewModel() {

    private val _state = MutableStateFlow(MediaAudioDetailState())
    val state: StateFlow<MediaAudioDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaAudioDetailEffect>()
    val effect: SharedFlow<MediaAudioDetailEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    fun handleIntent(intent: MediaAudioDetailIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaAudioDetailIntent.NavigateBack -> _effect.emit(MediaAudioDetailEffect.NavigateBack)
            is MediaAudioDetailIntent.LoadPlaylist -> loadPlaylist(intent.startAudioUri)
            is MediaAudioDetailIntent.OnMediaItemTransition -> _state.update { it.copy(currentIndex = intent.currentIndex) }
            is MediaAudioDetailIntent.UpdatePlayerState -> updatePlayerState(
                isPlaying = intent.isPlaying,
                isLoading = intent.isLoading,
                title = intent.title,
                artist = intent.artist,
                currentPosition = intent.currentPosition,
                duration = intent.duration,
                error = intent.error
            )
        }
    }

    private suspend fun loadPlaylist(startAudioUri: String) {
        try {
            _state.update { it.copy(isLoading = true, error = null) }

            val allAudios = getAudiosUseCase()
            val startIndex = allAudios.indexOfFirst { it.uri.toString() == startAudioUri }.takeIf { it >= 0 } ?: 0

            _state.update { it.copy(playlist = allAudios, currentIndex = startIndex, isLoading = false) }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun updatePlayerState(
        isPlaying: Boolean? = null,
        isLoading: Boolean? = null,
        title: String? = null,
        artist: String? = null,
        currentPosition: Long? = null,
        duration: Long? = null,
        error: String? = null
    ) {
        _state.update { current ->
            current.copy(
                isPlaying = isPlaying ?: current.isPlaying,
                isLoading = isLoading ?: current.isLoading,
                title = title ?: current.title,
                artist = artist ?: current.artist,
                currentPosition = currentPosition ?: current.currentPosition,
                duration = duration ?: current.duration,
                error = error ?: current.error
            )
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(MediaAudioDetailEffect.ShowError(errorMessage))
    }
}