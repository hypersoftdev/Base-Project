package com.hypersoft.baseproject.presentation.mediaAudioDetails.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.hypersoft.baseproject.presentation.main.MainActivity
import com.hypersoft.baseproject.core.R as coreR

@UnstableApi
class MediaNotificationManager(private val context: Context, private val mediaSession: MediaSession) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var playerNotificationManager: PlayerNotificationManager? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "music_player_channel"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createPlayerNotificationManager(player: Player): PlayerNotificationManager {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationListener = when (context) {
            is MediaSessionService -> NotificationListener(context)
            else -> NotificationListener(null)
        }

        playerNotificationManager = PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(MediaDescriptionAdapter(pendingIntent))
            .setNotificationListener(notificationListener)
            .setChannelImportance(NotificationManager.IMPORTANCE_LOW)
            .setSmallIconResourceId(coreR.drawable.ic_svg_play)
            .build()
            .apply {
                setMediaSessionToken(mediaSession.platformToken)
                setPlayer(player)
            }

        return playerNotificationManager!!
    }

    fun release() {
        playerNotificationManager?.setPlayer(null)
        playerNotificationManager = null
    }

    private class MediaDescriptionAdapter(private val pendingIntent: PendingIntent) : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return player.mediaMetadata.title?.toString() ?: "Unknown Title"
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return player.mediaMetadata.artist?.toString() ?: "Unknown Artist"
        }

        override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): android.graphics.Bitmap? {
            // Return null for now, can be enhanced with album art later
            return null
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return pendingIntent
        }
    }

    private class NotificationListener(private val service: MediaSessionService?) : PlayerNotificationManager.NotificationListener {

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            // Handle notification cancellation if needed
        }

        override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
            // Ensure service is promoted to foreground immediately to avoid ANR
            if (ongoing && service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    service.startForeground(notificationId, notification)
                } catch (ex: Exception) {
                    // Ignore if already foreground
                }
            }
        }
    }
}