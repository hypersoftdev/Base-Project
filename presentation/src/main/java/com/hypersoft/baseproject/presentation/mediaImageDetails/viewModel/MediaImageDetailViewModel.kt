package com.hypersoft.baseproject.presentation.mediaImageDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.mediaImageDetails.effect.MediaImageDetailEffect
import com.hypersoft.baseproject.presentation.mediaImageDetails.intent.MediaImageDetailIntent
import com.hypersoft.baseproject.presentation.mediaImageDetails.state.MediaImageDetailState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaImageDetailViewModel(private val imageUri: String) : ViewModel() {

    private val _state = MutableStateFlow(MediaImageDetailState(imageUri = imageUri))
    val state: StateFlow<MediaImageDetailState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MediaImageDetailEffect>()
    val effect: SharedFlow<MediaImageDetailEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: MediaImageDetailIntent) = viewModelScope.launch {
        when (intent) {
            is MediaImageDetailIntent.NavigateBack -> _effect.emit(MediaImageDetailEffect.NavigateBack)
        }
    }
}