package com.hypersoft.baseproject.presentation.mediaVideos.state

import com.hypersoft.baseproject.domain.media.entities.VideoEntity

data class MediaVideosState(
    val isLoading: Boolean = false,
    val videos: List<VideoEntity> = emptyList(),
    val error: String? = null
)