package com.hypersoft.baseproject.presentation.mediaVideoDetails.effect

sealed class MediaVideoDetailEffect {
    object NavigateBack : MediaVideoDetailEffect()
    data class ShowError(val message: String) : MediaVideoDetailEffect()
}