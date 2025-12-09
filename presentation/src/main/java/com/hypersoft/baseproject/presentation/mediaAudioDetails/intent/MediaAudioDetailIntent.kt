package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

/**
 * User intents for audio playback screen.
 */
sealed class MediaAudioDetailIntent {
    object NavigateBack : MediaAudioDetailIntent()
    object TogglePlayPause : MediaAudioDetailIntent()
    object SeekToNext : MediaAudioDetailIntent()
    object SeekToPrevious : MediaAudioDetailIntent()
    object Rewind : MediaAudioDetailIntent()
    object Forward : MediaAudioDetailIntent()
    object Repeat : MediaAudioDetailIntent()
    object Shuffle : MediaAudioDetailIntent()
    data class SeekTo(val positionMs: Long) : MediaAudioDetailIntent()
    data class LoadPlaylist(val startAudioUri: String) : MediaAudioDetailIntent()
    data class UpdatePlayerState(val snapshot: PlayerSnapshot) : MediaAudioDetailIntent()
}