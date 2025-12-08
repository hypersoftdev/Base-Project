package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

@UnstableApi
class ExoPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    fun getPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    true // Handle audio focus
                )
                .setHandleAudioBecomingNoisy(true)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                    playWhenReady = false
                }
        }
        return exoPlayer!!
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun isInitialized(): Boolean = exoPlayer != null
}