package com.hypersoft.baseproject.presentation.mediaAudioDetails.effect

/**
 * Side effects for audio playback screen.
 * One-time actions that need to be executed.
 */
sealed class MediaAudioDetailEffect {
    object NavigateBack : MediaAudioDetailEffect()
    object Play : MediaAudioDetailEffect()
    object Pause : MediaAudioDetailEffect()
    object AnimatePlayPauseButton : MediaAudioDetailEffect()
    object SeekToNext : MediaAudioDetailEffect()
    object SeekToPrevious : MediaAudioDetailEffect()
    object Rewind : MediaAudioDetailEffect()
    object Forward : MediaAudioDetailEffect()
    object Repeat : MediaAudioDetailEffect()
    object Shuffle : MediaAudioDetailEffect()
    data class SeekTo(val positionMs: Long) : MediaAudioDetailEffect()
    data class ShowError(val message: String) : MediaAudioDetailEffect()
}