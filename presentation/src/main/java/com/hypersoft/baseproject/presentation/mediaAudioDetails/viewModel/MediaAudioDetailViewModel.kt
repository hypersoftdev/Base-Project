package com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Simplified ViewModel that only handles:
 * - Loading audio list from use case
 * - Navigation effects
 * - Static UI data (title, artist from navArgs)
 *
 * All playback state comes from MediaController in the Fragment.
 */
class MediaAudioDetailViewModel(application: Application, private val getAudiosUseCase: GetAudiosUseCase) : AndroidViewModel(application) {

    private val _effect = MutableSharedFlow<MediaAudioDetailEffect>()
    val effect: SharedFlow<MediaAudioDetailEffect> = _effect.asSharedFlow()

    private var allAudios: List<AudioEntity> = emptyList()

    /**
     * Load all audios and find the starting audio.
     * Returns the full list and the index of the starting audio.
     */
    suspend fun loadPlaylist(startAudioUri: String): Pair<List<AudioEntity>, Int> {
        allAudios = getAudiosUseCase()
        val startIndex = allAudios.indexOfFirst { it.uri.toString() == startAudioUri }
        return Pair(allAudios, if (startIndex >= 0) startIndex else 0)
    }

    fun handleIntent(intent: MediaAudioDetailIntent) = viewModelScope.launch {
        when (intent) {
            is MediaAudioDetailIntent.NavigateBack -> {
                _effect.emit(MediaAudioDetailEffect.NavigateBack)
            }
        }
    }
}