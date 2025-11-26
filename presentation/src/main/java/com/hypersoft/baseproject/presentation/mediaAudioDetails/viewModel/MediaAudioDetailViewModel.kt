package com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel

import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.factory.MediaPlayerFactory
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaAudioDetailViewModel(
    private val playerFactory: MediaPlayerFactory,
    private val getAudiosUseCase: GetAudiosUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaAudioDetailState())
    val state: StateFlow<MediaAudioDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaAudioDetailEffect>()
    val effect: SharedFlow<MediaAudioDetailEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var audioEntity: AudioEntity? = null
    private var progressJob: Job? = null

    fun handleIntent(intent: MediaAudioDetailIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaAudioDetailIntent.LoadAudio -> initializeAudio(intent.audioUri)
            is MediaAudioDetailIntent.PlayPause -> togglePlayPause()
            is MediaAudioDetailIntent.NavigateBack -> navigateBack()
            is MediaAudioDetailIntent.SeekTo -> seekTo(intent.position)
        }
    }

    private suspend fun initializeAudio(audioUriString: String) {
        _state.update { it.copy(isLoading = true, error = null) }

        // Find audio entity from URI
        val audios = getAudiosUseCase()
        audioEntity = audios.find { it.uri.toString() == audioUriString }

        if (audioEntity == null) {
            handleError(Exception("Audio file not found"))
            return
        }

        // Initialize MediaPlayer using factory
        val uri = audioUriString.toUri()
        mediaPlayer?.release()
        mediaPlayer = playerFactory.create(uri).apply {
            prepareAsync()
            setOnPreparedListener {
                viewModelScope.launch {
                    val duration = mediaPlayer?.duration ?: 0
                    _state.update {
                        it.copy(isLoading = false, title = audioEntity?.displayName ?: "", artist = audioEntity?.artist ?: "Unknown Artist", duration = duration, error = null)
                    }
                }
            }
            setOnCompletionListener {
                viewModelScope.launch {
                    _state.update { it.copy(isPlaying = false, currentProgress = 0) }
                    stopProgressUpdates()
                }
            }
            setOnErrorListener { _, what, extra ->
                viewModelScope.launch {
                    handleError(Exception("MediaPlayer error: what=$what, extra=$extra"))
                }
                true
            }
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (_state.value.isPlaying) {
                player.pause()
                _state.update { it.copy(isPlaying = false) }
                stopProgressUpdates()
            } else {
                player.start()
                _state.update { it.copy(isPlaying = true) }
                startProgressUpdates()
            }
        }
    }

    private suspend fun navigateBack() {
        releaseMediaPlayer()
        _effect.emit(MediaAudioDetailEffect.NavigateBack)
    }

    private fun seekTo(position: Int) {
        mediaPlayer?.let { player ->
            player.seekTo(position)
            _state.update { it.copy(currentProgress = position) }
        }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive && mediaPlayer != null) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val currentPosition = player.currentPosition
                        _state.update { it.copy(currentProgress = currentPosition) }
                    }
                }
                delay(100) // Update every 100ms
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(MediaAudioDetailEffect.ShowError(errorMessage))
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        stopProgressUpdates()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }
}