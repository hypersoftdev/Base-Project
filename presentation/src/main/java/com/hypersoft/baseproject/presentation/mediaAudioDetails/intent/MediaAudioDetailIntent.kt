package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

sealed class MediaAudioDetailIntent {
    data class LoadAudio(val audioUri: String) : MediaAudioDetailIntent()
    object PlayPause : MediaAudioDetailIntent()
    object NavigateBack : MediaAudioDetailIntent()
    data class SeekTo(val position: Int) : MediaAudioDetailIntent()
}