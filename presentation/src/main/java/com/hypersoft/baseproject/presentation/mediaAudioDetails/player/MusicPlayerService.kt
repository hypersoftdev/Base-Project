package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.hypersoft.baseproject.core.R as coreR

@UnstableApi
class MusicPlayerService : MediaSessionService() {

    private val binder = LocalBinder()
    private var exoPlayerManager: ExoPlayerManager? = null
    private var mediaSession: MediaSession? = null
    private var notificationManager: MediaNotificationManager? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentAudioEntity: AudioEntity? = null
    private var audioQueue: List<AudioEntity> = emptyList()
    private var currentIndex: Int = -1
    private val progressHandler = Handler(Looper.getMainLooper())
    private var progressUpdateRunnable: Runnable? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = MediaNotificationManager.NOTIFICATION_CHANNEL_ID
        const val NOTIFICATION_ID = MediaNotificationManager.NOTIFICATION_ID
    }

    inner class LocalBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onCreate() {
        super.onCreate()

        // Create a placeholder notification channel and notification immediately to avoid ANR
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createPlaceholderNotification())

        exoPlayerManager = ExoPlayerManager(this)
        val player = exoPlayerManager!!.getPlayer()

        // Set up player listeners
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlaybackState()
                if (playbackState == Player.STATE_READY) {
                    // Auto-play when ready if playWhenReady is true
                    if (player.playWhenReady && !player.isPlaying) {
                        player.play()
                    }
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
                if (isPlaying) {
                    startProgressUpdates()
                } else {
                    stopProgressUpdates()
                }
            }

            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                updatePlaybackState()
                // Update metadata when track changes - ensure we have the latest entity
                currentAudioEntity?.let { entity ->
                    _playbackState.value = _playbackState.value.copy(
                        title = entity.displayName,
                        artist = entity.artist,
                        currentProgress = 0,
                        isLoading = player.playbackState == Player.STATE_BUFFERING
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val errorMessage = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Network error. Please check your connection."

                    PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
                    PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> "Unsupported audio format."

                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Audio file not found."
                    else -> error.message ?: "Playback error occurred (${error.errorCode})"
                }
                _playbackState.value = _playbackState.value.copy(
                    error = errorMessage,
                    isPlaying = false
                )
            }
        })

        // Create MediaSession
        mediaSession = MediaSession.Builder(this, player).build()

        // Create notification manager - this must be done before service can be started
        notificationManager = MediaNotificationManager(this, mediaSession!!)
        val playerNotificationManager = notificationManager!!.createPlayerNotificationManager(player)

        // Ensure notification is ready immediately to avoid ANR
        // The PlayerNotificationManager will post notification when player state changes
        // But we need to ensure it's set up before the service is considered "started"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Music Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createPlaceholderNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Preparing...")
            .setSmallIcon(coreR.drawable.ic_svg_play)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        // This ensures the service is promoted to foreground immediately
        // PlayerNotificationManager will handle the actual notification
        super.onUpdateNotification(session, startInForegroundRequired)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // MediaSessionService should automatically promote to foreground when MediaSession is active
        // But to avoid ANR, we ensure notification is ready
        // The PlayerNotificationManager will call startForeground via NotificationListener
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    fun loadAudio(audioEntity: AudioEntity, queue: List<AudioEntity> = emptyList()) {
        currentAudioEntity = audioEntity

        // Use provided queue, or create a single-item queue if empty
        audioQueue = queue.ifEmpty {
            listOf(audioEntity)
        }

        // Find and update current index in the queue
        val foundIndex = audioQueue.indexOfFirst { it.uri == audioEntity.uri }
        currentIndex = if (foundIndex >= 0) foundIndex else 0

        val player = exoPlayerManager!!.getPlayer()
        try {
            // Update metadata immediately
            _playbackState.value = _playbackState.value.copy(
                isLoading = true,
                title = audioEntity.displayName,
                artist = audioEntity.artist,
                error = null,
                currentProgress = 0
            )

            val mediaItem = androidx.media3.common.MediaItem.fromUri(audioEntity.uri)
                .buildUpon()
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(audioEntity.displayName)
                        .setArtist(audioEntity.artist)
                        .build()
                )
                .build()

            // Stop current playback if any
            if (player.isPlaying) {
                player.pause()
            }

            player.setMediaItem(mediaItem)
            // Set playWhenReady to true for auto-play when ready
            player.playWhenReady = true
            player.prepare()
            // Note: Player will automatically start playing when STATE_READY is reached
        } catch (e: IllegalStateException) {
            // Player might be in an invalid state, try to reset
            try {
                player.stop()
                _playbackState.value = _playbackState.value.copy(
                    isLoading = true,
                    title = audioEntity.displayName,
                    artist = audioEntity.artist,
                    error = null,
                    currentProgress = 0
                )

                val mediaItem = androidx.media3.common.MediaItem.fromUri(audioEntity.uri)
                    .buildUpon()
                    .setMediaMetadata(
                        androidx.media3.common.MediaMetadata.Builder()
                            .setTitle(audioEntity.displayName)
                            .setArtist(audioEntity.artist)
                            .build()
                    )
                    .build()

                player.setMediaItem(mediaItem)
                // Set playWhenReady to true for auto-play when ready
                player.playWhenReady = true
                player.prepare()
                // Note: Player will automatically start playing when STATE_READY is reached
            } catch (retryException: Exception) {
                _playbackState.value = _playbackState.value.copy(
                    error = retryException.message ?: "Failed to load audio: ${retryException.javaClass.simpleName}"
                )
            }
        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(
                error = e.message ?: "Failed to load audio: ${e.javaClass.simpleName}"
            )
        }
    }

    fun playPause() {
        val player = exoPlayerManager!!.getPlayer()
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
        updatePlaybackState()
    }

    fun seekTo(positionMs: Int) {
        val player = exoPlayerManager!!.getPlayer()
        player.seekTo(positionMs.toLong())
        updatePlaybackState()
    }

    fun skipToNext() {
        if (audioQueue.isEmpty()) {
            return
        }

        if (currentIndex < audioQueue.size - 1) {
            currentIndex++
            val nextEntity = audioQueue[currentIndex]
            currentAudioEntity = nextEntity
            // Update state immediately for UI responsiveness
            _playbackState.value = _playbackState.value.copy(
                isLoading = true,
                title = nextEntity.displayName,
                artist = nextEntity.artist,
                currentProgress = 0
            )
            loadAudio(nextEntity, audioQueue)
        }
    }

    fun skipToPrevious() {
        if (audioQueue.isEmpty()) {
            return
        }

        val player = exoPlayerManager!!.getPlayer()
        val currentPosition = player.currentPosition

        // If more than 3 seconds into track, restart current track
        if (currentPosition > 3000) {
            seekTo(0)
        } else if (currentIndex > 0) {
            currentIndex--
            val previousEntity = audioQueue[currentIndex]
            currentAudioEntity = previousEntity
            // Update state immediately for UI responsiveness
            _playbackState.value = _playbackState.value.copy(
                isLoading = true,
                title = previousEntity.displayName,
                artist = previousEntity.artist,
                currentProgress = 0
            )
            loadAudio(previousEntity, audioQueue)
        } else if (currentIndex == 0 && audioQueue.isNotEmpty()) {
            // If at first track, restart it
            seekTo(0)
        }
    }

    fun rewind(seconds: Int = 5) {
        val player = exoPlayerManager!!.getPlayer()
        val newPosition = (player.currentPosition - (seconds * 1000)).coerceAtLeast(0)
        seekTo(newPosition.toInt())
    }

    fun forward(seconds: Int = 15) {
        val player = exoPlayerManager!!.getPlayer()
        val duration = player.duration
        if (duration != C.TIME_UNSET && duration > 0) {
            val newPosition = (player.currentPosition + (seconds * 1000)).coerceAtMost(duration)
            seekTo(newPosition.toInt())
        }
    }

    private fun updatePlaybackState() {
        val player = exoPlayerManager!!.getPlayer()
        val duration = if (player.duration != C.TIME_UNSET && player.duration > 0) {
            player.duration.toInt()
        } else {
            0
        }
        val currentPosition = player.currentPosition.toInt()
        val isPlaying = player.isPlaying
        val isLoading = player.playbackState == Player.STATE_BUFFERING ||
                player.playbackState == Player.STATE_IDLE

        _playbackState.value = _playbackState.value.copy(
            isPlaying = isPlaying,
            currentProgress = currentPosition,
            duration = duration,
            isLoading = isLoading
        )
    }

    private fun startProgressUpdates() {
        stopProgressUpdates() // Cancel any existing updates
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                val player = exoPlayerManager?.getPlayer()
                if (player != null && player.isPlaying) {
                    updatePlaybackState()
                    progressHandler.postDelayed(this, 100) // Update every 100ms
                }
            }
        }
        progressHandler.post(progressUpdateRunnable!!)
    }

    private fun stopProgressUpdates() {
        progressUpdateRunnable?.let {
            progressHandler.removeCallbacks(it)
            progressUpdateRunnable = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgressUpdates()
        progressHandler.removeCallbacksAndMessages(null)
        notificationManager?.release()
        mediaSession?.run {
            player.release()
            release()
        }
        exoPlayerManager?.release()
        mediaSession = null
        notificationManager = null
        exoPlayerManager = null
    }

    data class PlaybackState(
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false,
        val title: String = "",
        val artist: String = "",
        val currentProgress: Int = 0,
        val duration: Int = 0,
        val error: String? = null
    )
}