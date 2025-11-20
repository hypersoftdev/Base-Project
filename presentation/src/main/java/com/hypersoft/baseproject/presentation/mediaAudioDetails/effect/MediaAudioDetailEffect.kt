package com.hypersoft.baseproject.presentation.mediaAudioDetails.effect

sealed class MediaAudioDetailEffect {
    object NavigateBack : MediaAudioDetailEffect()
    data class ShowError(val message: String) : MediaAudioDetailEffect()
}