package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

data class PlayerSnapshot(
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val title: String?,
    val artist: String?,
    val position: Long,
    val duration: Long,
    val currentIndex: Int,
    val repeatMode: Int,
    val shuffleModeEnabled: Boolean
)