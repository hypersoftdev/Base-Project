package com.hypersoft.baseproject.presentation.mediaVideos.effect

sealed class MediaVideosEffect {
    object NavigateBack : MediaVideosEffect()
    object GrantPermissionClick : MediaVideosEffect()
    data class NavigateToDetail(val videoUri: String) : MediaVideosEffect()
    data class ShowError(val message: String) : MediaVideosEffect()
}