package com.hypersoft.baseproject.presentation.mediaVideos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetVideosUseCase
import com.hypersoft.baseproject.domain.media.useCases.contentObserver.ObserveMediaChangesUseCase
import com.hypersoft.baseproject.presentation.mediaVideos.effect.MediaVideosEffect
import com.hypersoft.baseproject.presentation.mediaVideos.enums.MediaVideosPermissionLevel
import com.hypersoft.baseproject.presentation.mediaVideos.intent.MediaVideosIntent
import com.hypersoft.baseproject.presentation.mediaVideos.state.MediaVideosState
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

class MediaVideosViewModel(
    private val getVideosUseCase: GetVideosUseCase,
    private val observeMediaChangesUseCase: ObserveMediaChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaVideosState())
    val state: StateFlow<MediaVideosState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaVideosEffect>()
    val effect: SharedFlow<MediaVideosEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private var observeJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeMediaStore()
        handleIntent(MediaVideosIntent.LoadVideos)
    }

    fun handleIntent(intent: MediaVideosIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaVideosIntent.NavigationBack -> _effect.emit(MediaVideosEffect.NavigateBack)
            is MediaVideosIntent.LoadVideos -> loadVideos()
            is MediaVideosIntent.GrantPermissionClick -> _effect.emit(MediaVideosEffect.GrantPermissionClick)
            is MediaVideosIntent.PermissionChanged -> onPermissionChanged(intent.level)
            is MediaVideosIntent.VideoClicked -> _effect.emit(MediaVideosEffect.NavigateToDetail(intent.videoUri))
        }
    }

    private fun observeMediaStore() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeMediaChangesUseCase
                .observeVideos()
                .collectLatest {
                    handleIntent(MediaVideosIntent.LoadVideos)
                }
        }
    }

    private fun loadVideos() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(coroutineExceptionHandler) {
            _state.update { it.copy(isLoading = true) }

            val videos = getVideosUseCase()
            _state.update {
                it.copy(isLoading = false, videos = videos)
            }
        }
    }

    private fun onPermissionChanged(level: MediaVideosPermissionLevel) {
        val isPermissionIdle = state.value.permission == MediaVideosPermissionLevel.Idle
        val isStateSame = (level == MediaVideosPermissionLevel.Full) && (level == state.value.permission)

        _state.update { it.copy(permission = level) }
        if (isPermissionIdle || isStateSame) return

        if (level == MediaVideosPermissionLevel.Full || level == MediaVideosPermissionLevel.Limited) {
            loadVideos()
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(MediaVideosEffect.ShowError(errorMessage))
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
        loadJob?.cancel()
    }
}