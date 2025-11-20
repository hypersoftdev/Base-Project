package com.hypersoft.baseproject.presentation.media.effect

sealed class MediaEffect {
    object NavigateBack : MediaEffect()
    object NavigateToImages : MediaEffect()
    object NavigateToVideos : MediaEffect()
    object NavigateToAudios : MediaEffect()
    data class ShowError(val message: String) : MediaEffect()
}