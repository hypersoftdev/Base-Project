package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * MediaSessionService for background audio playback.
 * Follows Media3 best practices:
 * - Creates ExoPlayer in onCreate()
 * - Creates MediaSession with the player
 * - Exposes MediaSession via onGetSession()
 * - Uses default notification (MediaSessionService handles it automatically)
 */
@UnstableApi
class PlaybackService : MediaSessionService() {

    private var exoPlayerManager: ExoPlayerManager? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        initMediaSession()
    }

    private fun initMediaSession() {
        exoPlayerManager = ExoPlayerManager(this).apply {
            mediaSession = MediaSession.Builder(this@PlaybackService, getPlayer()).build()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        exoPlayerManager?.run {
            release()
            exoPlayerManager = null
        }
    }
}