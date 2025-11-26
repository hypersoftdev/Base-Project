package com.hypersoft.baseproject.presentation.mediaAudioDetails.factory

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class MediaPlayerFactory(private val context: Context) {

    fun create(uri: Uri): MediaPlayer {
        return MediaPlayer().apply {
            setDataSource(context, uri)
        }
    }
}