package com.hypersoft.baseproject.presentation.mediaImages.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetImageFoldersUseCase
import com.hypersoft.baseproject.domain.media.useCases.contentObserver.ObserveMediaChangesUseCase
import com.hypersoft.baseproject.presentation.mediaImages.effect.MediaImagesEffect
import com.hypersoft.baseproject.presentation.mediaImages.intent.MediaImagesIntent
import com.hypersoft.baseproject.presentation.mediaImages.state.MediaImagesState
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

class MediaImagesViewModel(
    private val getImageFoldersUseCase: GetImageFoldersUseCase,
    private val observeMediaChangesUseCase: ObserveMediaChangesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MediaImagesState())
    val state: StateFlow<MediaImagesState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaImagesEffect>()
    val effect: SharedFlow<MediaImagesEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    private var observeJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeMediaStore()
        handleIntent(MediaImagesIntent.LoadFolders)
    }

    fun handleIntent(intent: MediaImagesIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is MediaImagesIntent.LoadFolders -> loadFolders()
            is MediaImagesIntent.RefreshFolders -> loadFolders()
            is MediaImagesIntent.ImageClicked -> _effect.emit(MediaImagesEffect.NavigateToDetail(intent.imageUri))
        }
    }

    private fun observeMediaStore() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            observeMediaChangesUseCase
                .observeImages()
                .collectLatest {
                    handleIntent(MediaImagesIntent.LoadFolders)
                }
        }
    }

    private fun loadFolders() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(coroutineExceptionHandler) {
            _state.update { it.copy(isLoading = true, error = null) }

            val folders = getImageFoldersUseCase()
            _state.update {
                it.copy(isLoading = false, folders = folders, error = null)
            }
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false, error = errorMessage) }
        _effect.emit(MediaImagesEffect.ShowError(errorMessage))
    }

    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
        loadJob?.cancel()
    }
}