package com.hypersoft.baseproject.presentation.mediaAudioDetails.state

data class MediaAudioDetailState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val currentProgress: Int = 0,
    val duration: Int = 0,
    val error: String? = null,
    val queueSize: Int = 0,
    val currentQueueIndex: Int = -1,
    val isShuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
) {
    enum class RepeatMode {
        OFF,
        ONE,
        ALL
    }
}