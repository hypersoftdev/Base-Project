package com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.player.MusicPlayerService
import com.hypersoft.baseproject.presentation.mediaAudioDetails.player.MusicPlayerServiceConnection
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class MediaAudioDetailViewModel(application: Application, private val getAudiosUseCase: GetAudiosUseCase) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(MediaAudioDetailState())
    val state: StateFlow<MediaAudioDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaAudioDetailEffect>()
    val effect: SharedFlow<MediaAudioDetailEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private val serviceConnection = MusicPlayerServiceConnection(application)
    private var currentAudioEntity: AudioEntity? = null
    private var allAudios: List<AudioEntity> = emptyList()

    init {
        serviceConnection.bindService()
        observeServiceState()
    }

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    private fun observeServiceState() {
        serviceConnection.isServiceConnected
            .flatMapLatest { isConnected ->
                if (isConnected) {
                    val service = serviceConnection.getService()
                    service?.playbackState ?: flowOf(MusicPlayerService.PlaybackState())
                } else {
                    flowOf(MusicPlayerService.PlaybackState())
                }
            }
            .onEach { playbackState ->
                _state.update { state ->
                    state.copy(
                        isLoading = playbackState.isLoading,
                        isPlaying = playbackState.isPlaying,
                        title = playbackState.title.ifEmpty { state.title },
                        artist = playbackState.artist.ifEmpty { state.artist },
                        currentProgress = playbackState.currentProgress,
                        duration = playbackState.duration,
                        error = playbackState.error
                    )
                }

                playbackState.error?.let { error ->
                    _effect.emit(MediaAudioDetailEffect.ShowError(error))
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleIntent(intent: MediaAudioDetailIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaAudioDetailIntent.LoadAudio -> initializeAudio(intent.audioUri, intent.queue)
            is MediaAudioDetailIntent.PlayPause -> togglePlayPause()
            is MediaAudioDetailIntent.NavigateBack -> navigateBack()
            is MediaAudioDetailIntent.SeekTo -> seekTo(intent.position)
            is MediaAudioDetailIntent.SkipToNext -> skipToNext()
            is MediaAudioDetailIntent.SkipToPrevious -> skipToPrevious()
            is MediaAudioDetailIntent.Rewind -> rewind(intent.seconds)
            is MediaAudioDetailIntent.Forward -> forward(intent.seconds)
        }
    }

    private suspend fun initializeAudio(audioUriString: String, queueUris: List<String>) {
        _state.update { it.copy(isLoading = true, error = null) }

        // Get all audios - this will be used as the queue
        allAudios = getAudiosUseCase()
        currentAudioEntity = allAudios.find { it.uri.toString() == audioUriString }

        if (currentAudioEntity == null) {
            handleError(Exception("Audio file not found"))
            return
        }

        // Use provided queue if available, otherwise use full audio list as queue
        val queue = if (queueUris.isNotEmpty()) {
            queueUris.mapNotNull { uri ->
                allAudios.find { it.uri.toString() == uri }
            }
        } else {
            // Use full audio list as queue - this is what the user wants
            allAudios
        }

        // Find current index in the queue
        val currentIndex = queue.indexOfFirst { it.uri == currentAudioEntity!!.uri }
            .takeIf { it >= 0 } ?: 0

        // Wait for service to be connected, then load audio
        viewModelScope.launch {
            // Wait a bit for service to connect
            var attempts = 0
            while (attempts < 50 && serviceConnection.getService() == null) {
                kotlinx.coroutines.delay(100)
                attempts++
            }

            val service = serviceConnection.getService()
            if (service != null && currentAudioEntity != null) {
                service.loadAudio(currentAudioEntity!!, queue)

                _state.update {
                    it.copy(
                        queueSize = queue.size,
                        currentQueueIndex = currentIndex
                    )
                }
            } else {
                handleError(Exception("Service not available"))
            }
        }
    }

    private fun togglePlayPause() {
        serviceConnection.getService()?.playPause()
    }

    private suspend fun navigateBack() {
        serviceConnection.unbindService()
        _effect.emit(MediaAudioDetailEffect.NavigateBack)
    }

    private fun seekTo(position: Int) {
        serviceConnection.getService()?.seekTo(position)
    }

    private fun skipToNext() {
        val service = serviceConnection.getService()
        if (service != null) {
            service.skipToNext()
            // Update UI state from service after a brief delay to allow service to update
            viewModelScope.launch {
                kotlinx.coroutines.delay(50)
                // State will be updated via observeServiceState flow
            }
        }
    }

    private fun skipToPrevious() {
        val service = serviceConnection.getService()
        if (service != null) {
            service.skipToPrevious()
            // Update UI state from service after a brief delay to allow service to update
            viewModelScope.launch {
                kotlinx.coroutines.delay(50)
                // State will be updated via observeServiceState flow
            }
        }
    }

    private fun rewind(seconds: Int) {
        serviceConnection.getService()?.rewind(seconds)
    }

    private fun forward(seconds: Int) {
        serviceConnection.getService()?.forward(seconds)
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(MediaAudioDetailEffect.ShowError(errorMessage))
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unbindService()
    }
}