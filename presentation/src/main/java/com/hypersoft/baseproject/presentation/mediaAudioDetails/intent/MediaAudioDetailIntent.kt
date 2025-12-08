package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

/**
 * User intents for audio playback screen.
 */
sealed class MediaAudioDetailIntent {
    object NavigateBack : MediaAudioDetailIntent()
    data class LoadPlaylist(val startAudioUri: String) : MediaAudioDetailIntent()
    data class PlayerSnapshot(val snapshot: PlayerSnapshot) : MediaAudioDetailIntent()
}