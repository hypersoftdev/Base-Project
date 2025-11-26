package com.hypersoft.baseproject.presentation.mediaVideoDetails.viewModel

import android.media.MediaPlayer
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import com.hypersoft.baseproject.domain.media.useCases.GetVideosUseCase
import com.hypersoft.baseproject.presentation.mediaVideoDetails.effect.MediaVideoDetailEffect
import com.hypersoft.baseproject.presentation.mediaVideoDetails.factory.MediaPlayerFactory
import com.hypersoft.baseproject.presentation.mediaVideoDetails.intent.MediaVideoDetailIntent
import com.hypersoft.baseproject.presentation.mediaVideoDetails.state.MediaVideoDetailState
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

class MediaVideoDetailViewModel(
    private val playerFactory: MediaPlayerFactory,
    private val getVideosUseCase: GetVideosUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaVideoDetailState())
    val state: StateFlow<MediaVideoDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaVideoDetailEffect>()
    val effect: SharedFlow<MediaVideoDetailEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var videoEntity: VideoEntity? = null
    private var progressJob: Job? = null

    fun handleIntent(intent: MediaVideoDetailIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaVideoDetailIntent.LoadVideo -> initializeVideo(intent.videoUri)
            is MediaVideoDetailIntent.PlayPause -> togglePlayPause()
            is MediaVideoDetailIntent.NavigateBack -> navigateBack()
            is MediaVideoDetailIntent.SeekTo -> seekTo(intent.position)
        }
    }

    private suspend fun initializeVideo(videoUriString: String) {
        _state.update { it.copy(isLoading = true, error = null) }

        // Find video entity from URI
        val videos = getVideosUseCase()
        videoEntity = videos.find { it.uri.toString() == videoUriString }

        if (videoEntity == null) {
            handleError(Exception("Video file not found"))
            return
        }

        // Initialize MediaPlayer using factory
        val uri = videoUriString.toUri()
        mediaPlayer?.release()
        mediaPlayer = playerFactory.create(uri).apply {
            prepareAsync()
            setOnPreparedListener {
                viewModelScope.launch {
                    val duration = mediaPlayer?.duration ?: 0
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = videoEntity?.displayName ?: "",
                            duration = duration,
                            error = null
                        )
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
        _effect.emit(MediaVideoDetailEffect.NavigateBack)
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
        _effect.emit(MediaVideoDetailEffect.ShowError(errorMessage))
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