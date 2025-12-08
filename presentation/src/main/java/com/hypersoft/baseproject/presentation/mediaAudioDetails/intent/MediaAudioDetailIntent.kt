package com.hypersoft.baseproject.presentation.mediaAudioDetails.intent

/**
 * Simplified Intent - only navigation remains.
 * All playback controls are handled directly via MediaController in the Fragment.
 */
sealed class MediaAudioDetailIntent {
    object NavigateBack : MediaAudioDetailIntent()
}
