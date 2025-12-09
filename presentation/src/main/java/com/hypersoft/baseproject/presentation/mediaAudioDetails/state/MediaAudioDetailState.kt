package com.hypersoft.baseproject.presentation.mediaAudioDetails.state

import androidx.media3.common.Player
import com.hypersoft.baseproject.domain.media.entities.AudioEntity

/**
 * UI state for audio playback screen.
 * All state comes from MediaController player events and playlist data.
 */
data class MediaAudioDetailState(
    val playlist: List<AudioEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffleModeEnabled: Boolean = false,
    val error: String? = null
)