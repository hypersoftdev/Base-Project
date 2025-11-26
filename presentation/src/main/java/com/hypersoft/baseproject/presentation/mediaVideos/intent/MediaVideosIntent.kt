package com.hypersoft.baseproject.presentation.mediaVideos.intent

sealed class MediaVideosIntent {
    object LoadVideos : MediaVideosIntent()
    data class VideoClicked(val videoUri: String) : MediaVideosIntent()
}