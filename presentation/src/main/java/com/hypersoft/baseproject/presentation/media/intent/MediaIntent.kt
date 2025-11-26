package com.hypersoft.baseproject.presentation.media.intent

sealed class MediaIntent {
    object NavigateBack : MediaIntent()
    object NavigateToImages : MediaIntent()
    object NavigateToVideos : MediaIntent()
    object NavigateToAudios : MediaIntent()
}