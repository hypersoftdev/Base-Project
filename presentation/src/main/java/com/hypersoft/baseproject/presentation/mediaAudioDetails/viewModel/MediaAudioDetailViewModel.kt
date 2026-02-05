package com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.PlayerSnapshot
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
 * - Emits effects for one-time actions (playback controls, navigation, errors)
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
            is MediaAudioDetailIntent.TogglePlayPause -> onTogglePlayPause()
            is MediaAudioDetailIntent.LoadPlaylist -> loadPlaylist(intent.startAudioUri)
            is MediaAudioDetailIntent.SeekToNext -> _effect.emit(MediaAudioDetailEffect.SeekToNext)
            is MediaAudioDetailIntent.SeekToPrevious -> _effect.emit(MediaAudioDetailEffect.SeekToPrevious)
            is MediaAudioDetailIntent.Rewind -> _effect.emit(MediaAudioDetailEffect.Rewind)
            is MediaAudioDetailIntent.Forward -> _effect.emit(MediaAudioDetailEffect.Forward)
            is MediaAudioDetailIntent.SeekTo -> _effect.emit(MediaAudioDetailEffect.SeekTo(intent.positionMs))
            is MediaAudioDetailIntent.Repeat -> _effect.emit(MediaAudioDetailEffect.Repeat)
            is MediaAudioDetailIntent.Shuffle -> _effect.emit(MediaAudioDetailEffect.Shuffle)
            is MediaAudioDetailIntent.UpdatePlayerState -> updateFromPlayer(intent.snapshot)
        }
    }

    private suspend fun onTogglePlayPause() {
        _effect.emit(MediaAudioDetailEffect.AnimatePlayPauseButton)
        when (_state.value.isPlaying) {
            true -> _effect.emit(MediaAudioDetailEffect.Pause)
            false -> _effect.emit(MediaAudioDetailEffect.Play)
        }
    }

    private suspend fun loadPlaylist(startUri: String) {
        _state.update { it.copy(isLoading = true) }
        val list = getAudiosUseCase()
        val index = list.indexOfFirst { it.uri.toString() == startUri }.coerceAtLeast(0)

        _state.update { it.copy(playlist = list, currentIndex = index, isLoading = false) }
    }

    private fun updateFromPlayer(snapshot: PlayerSnapshot) {
        _state.update {
            it.copy(
                isPlaying = snapshot.isPlaying,
                isLoading = snapshot.isLoading,
                title = snapshot.title ?: it.title,
                artist = snapshot.artist ?: it.artist,
                currentPosition = snapshot.position,
                duration = snapshot.duration,
                currentIndex = snapshot.currentIndex,
                repeatMode = snapshot.repeatMode,
                shuffleModeEnabled = snapshot.shuffleModeEnabled
            )
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(MediaAudioDetailEffect.ShowError(errorMessage))
    }
}