package com.hypersoft.baseproject.presentation.mediaVideoDetails.intent

sealed class MediaVideoDetailIntent {
    data class LoadVideo(val videoUri: String) : MediaVideoDetailIntent()
    object PlayPause : MediaVideoDetailIntent()
    object NavigateBack : MediaVideoDetailIntent()
    data class SeekTo(val position: Int) : MediaVideoDetailIntent()
}