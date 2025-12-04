package com.hypersoft.baseproject.presentation.mediaVideos.intent

import com.hypersoft.baseproject.presentation.mediaVideos.enums.MediaVideosPermissionLevel

sealed class MediaVideosIntent {
    object NavigationBack : MediaVideosIntent()
    object LoadVideos : MediaVideosIntent()
    object GrantPermissionClick : MediaVideosIntent()
    data class PermissionChanged(val level: MediaVideosPermissionLevel) : MediaVideosIntent()
    data class VideoClicked(val videoUri: String) : MediaVideosIntent()
}