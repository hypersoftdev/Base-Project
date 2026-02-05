package com.hypersoft.baseproject.presentation.mediaImagesTab.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.useCases.GetImagesUseCase
import com.hypersoft.baseproject.presentation.mediaImagesTab.effect.ImagesTabEffect
import com.hypersoft.baseproject.presentation.mediaImagesTab.intent.ImagesTabIntent
import com.hypersoft.baseproject.presentation.mediaImagesTab.state.ImagesTabState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImagesTabViewModel(
    private val getImagesUseCase: GetImagesUseCase,
    private val folderName: String
) : ViewModel() {

    private val _state = MutableStateFlow(ImagesTabState())
    val state: StateFlow<ImagesTabState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ImagesTabEffect>()
    val effect: SharedFlow<ImagesTabEffect> = _effect.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            handleError(exception)
        }
    }

    init {
        handleIntent(ImagesTabIntent.LoadImages)
    }

    fun handleIntent(intent: ImagesTabIntent) = viewModelScope.launch(coroutineExceptionHandler) {
        when (intent) {
            is ImagesTabIntent.LoadImages -> loadImages()
            is ImagesTabIntent.ImageClicked -> _effect.emit(ImagesTabEffect.NavigateToDetail(intent.imageUri))
        }
    }

    private suspend fun loadImages() {
        _state.update { it.copy(isLoading = true) }

        val images = getImagesUseCase(folderName)
        _state.update {
            it.copy(isLoading = false, images = images)
        }
    }

    private suspend fun handleError(exception: Throwable) {
        val errorMessage = exception.message ?: "An unexpected error occurred"
        _state.update { it.copy(isLoading = false) }
        _effect.emit(ImagesTabEffect.ShowError(errorMessage))
    }
}