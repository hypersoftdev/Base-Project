package com.hypersoft.baseproject.presentation.mediaVideos.state

import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import com.hypersoft.baseproject.presentation.mediaVideos.enums.MediaVideosPermissionLevel

data class MediaVideosState(
    val isLoading: Boolean = false,
    val videos: List<VideoEntity> = emptyList(),
    val permission: MediaVideosPermissionLevel = MediaVideosPermissionLevel.Idle
)