package com.hypersoft.baseproject.data.dataSources.mediaStore.contentObservers

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MediaStoreObserver(private val contentResolver: ContentResolver) {

    private val imagesUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    private val videosUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    private val audioUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    fun observeAllMediaChanges(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (uri != null) {
                    val uriString = uri.toString()
                    if (uriString.contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) ||
                        uriString.contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) ||
                        uriString.contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())
                    ) {
                        trySend(Unit)
                    }
                }
            }
        }

        contentResolver.registerContentObserver(imagesUri, true, observer)
        contentResolver.registerContentObserver(videosUri, true, observer)
        contentResolver.registerContentObserver(audioUri, true, observer)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    fun observeAudiosChanges(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (uri != null && (uri == audioUri || uri.toString().contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()))) {
                    trySend(Unit)
                }
            }
        }

        contentResolver.registerContentObserver(audioUri, true, observer)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    fun observeImagesChanges(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (uri != null && (uri == imagesUri || uri.toString().contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()))) {
                    trySend(Unit)
                }
            }
        }

        contentResolver.registerContentObserver(imagesUri, true, observer)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    fun observeVideosChanges(): Flow<Unit> = callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                if (uri != null && (uri == videosUri || uri.toString().contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()))) {
                    trySend(Unit)
                }
            }
        }

        contentResolver.registerContentObserver(videosUri, true, observer)

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }
}