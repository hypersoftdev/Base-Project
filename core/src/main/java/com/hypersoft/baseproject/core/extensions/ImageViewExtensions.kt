package com.hypersoft.baseproject.core.extensions

import android.content.ContentUris
import android.provider.MediaStore
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Example:
 *
 * imageView.loadImage(
 *     source = R.drawable.sample,
 *     placeholder = R.drawable.ic_placeholder,
 *     error = R.drawable.ic_broken
 * )
 *
 * imageView.loadImage(uri)  // no placeholder/error → totally fine
 * imageView.loadImage("https://example.com/pic.jpg", error = R.drawable.ic_error)
 */
fun ImageView.loadImage(
    source: Any?,
    crossFadeDuration: Int = 300,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) {
    val request = Glide.with(this)
        .load(source)
        .transition(DrawableTransitionOptions.withCrossFade(crossFadeDuration))

    placeholder?.let { request.placeholder(it) }
    error?.let { request.error(it) }

    request.into(this)
}

fun ImageView.loadAlbumArt(
    audioId: Long?,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) {
    if (audioId == null) {
        loadImage(source = placeholder, placeholder = placeholder, error = error)
        return
    }

    var albumId = 0L
    val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
    val selection = "${MediaStore.Audio.Media._ID} = ?"
    val selectionArgs = arrayOf(audioId.toString())

    context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            albumId = cursor.getLong(albumIdColumn)
        }
    }
    if (albumId == 0L) {
        loadImage(source = placeholder, placeholder = placeholder, error = error)
        return
    }
    val albumArtUri = ContentUris.withAppendedId("content://media/external/audio/albumart".toUri(), albumId)
    loadImage(source = albumArtUri, placeholder = placeholder, error = error)
}