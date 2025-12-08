package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

/**
 * User intents for audio playback screen.
 */
sealed class MediaAudioDetailIntent {
    object NavigateBack : MediaAudioDetailIntent()
    data class LoadPlaylist(val startAudioUri: String) : MediaAudioDetailIntent()
    data class OnMediaItemTransition(val currentIndex: Int) : MediaAudioDetailIntent()
    data class UpdatePlayerState(
        val isPlaying: Boolean? = null,
        val isLoading: Boolean? = null,
        val title: String? = null,
        val artist: String? = null,
        val currentPosition: Long? = null,
        val duration: Long? = null,
        val error: String? = null
    ) : MediaAudioDetailIntent()
}