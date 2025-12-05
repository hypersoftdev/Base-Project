package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.hypersoft.baseproject.domain.media.entities.AudioEntity

class ExoPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null

    fun getPlayer(): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context)
                .build()
                .apply {
                    repeatMode = Player.REPEAT_MODE_OFF
                    playWhenReady = false
                }
        }
        return exoPlayer!!
    }

    fun prepareMedia(audioEntity: AudioEntity, onPrepared: () -> Unit, onError: (Throwable) -> Unit) {
        val player = getPlayer()
        try {
            val mediaItem = MediaItem.fromUri(audioEntity.uri)
            player.setMediaItem(mediaItem)
            player.prepare()

            // Wait for player to be ready or handle errors
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            onPrepared()
                            player.removeListener(this)
                        }

                        Player.STATE_IDLE -> {
                            // Check if there's an error
                            val error = player.playerError
                            if (error != null) {
                                onError(error)
                                player.removeListener(this)
                            }
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    onError(error)
                    player.removeListener(this)
                }
            })
        } catch (ex: IllegalStateException) {
            // Handle cases where player is already prepared
            if (player.playbackState == Player.STATE_READY) {
                onPrepared()
            } else {
                onError(ex)
            }
        } catch (ex: Exception) {
            onError(ex)
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun isInitialized(): Boolean = exoPlayer != null
}