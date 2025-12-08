package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * MediaSessionService for background audio playback.
 * Follows Media3 official architecture:
 * - Creates ExoPlayer in onCreate()
 * - Creates MediaSession with the player
 * - Exposes MediaSession via onGetSession()
 * - Uses default notification (MediaSessionService handles automatically)
 */
@UnstableApi
class PlaybackService : MediaSessionService() {

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        // Create ExoPlayer with audio attributes for proper audio focus handling
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true // Handle audio focus automatically
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
            }

        // Create MediaSession - this automatically handles notifications
        mediaSession = MediaSession.Builder(this, player!!).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        // Accept all controller connections
        return mediaSession
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaSession and Player
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        player = null
    }
}