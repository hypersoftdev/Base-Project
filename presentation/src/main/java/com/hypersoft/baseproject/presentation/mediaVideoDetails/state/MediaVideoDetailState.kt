package com.hypersoft.baseproject.presentation.mediaVideoDetails.state

data class MediaVideoDetailState(
    val isLoading: Boolean = false,
    val isPlaying: Boolean = false,
    val title: String = "",
    val currentProgress: Int = 0,
    val duration: Int = 0,
    val error: String? = null
)