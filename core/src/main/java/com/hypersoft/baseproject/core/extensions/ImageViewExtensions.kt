package com.hypersoft.baseproject.core.extensions

import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

/**
 * Applies background color extracted from album art using Palette API.
 * Extracts vibrant colors from the album art thumbnail and applies them as the view's background.
 *
 * @param audioId The audio file ID from MediaStore
 * @param defaultColor Default background color if album art is not found (default: BLACK)
 * @param blendRatio Ratio to blend extracted color with black for readability (0.0-1.0, default: 0.4)
 * @param alpha Alpha value for the background color (0-255, default: 230)
 */
suspend fun View.applyGradientBackgroundFromAlbumArt(
    audioId: Long?,
    defaultColor: Int = Color.BLACK,
    alpha: Int = 255,
    gradientType: Int = GradientDrawable.Orientation.TOP_BOTTOM.ordinal,
    blendRatio: Float = 0.35f
) {
    if (audioId == null) {
        setBackgroundColor(defaultColor)
        return
    }

    val resolver = context.contentResolver

    // 1) Query albumId from MediaStore (IO thread)
    val albumId = withContext(Dispatchers.IO) {
        var id = 0L
        val projection = arrayOf(MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val args = arrayOf(audioId.toString())

        resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                id = cursor.getLong(idx)
            }
        }
        id
    }

    if (albumId == 0L) {
        setBackgroundColor(defaultColor)
        return
    }

    // 2) Build album art URI
    val albumArtUri = ContentUris.withAppendedId("content://media/external/audio/albumart".toUri(), albumId)

    // 3) Load bitmap via Glide (lifecycle-aware)
    withContext(Dispatchers.Main) {
        Glide.with(context)
            .asBitmap()
            .load(albumArtUri)
            .dontAnimate()
            .dontTransform()
            .placeholder(null)
            .apply {
                // force recalc per audioId
                signature(ObjectKey("album-$audioId"))
            }
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->

                        val dominant = palette?.getDominantColor(defaultColor) ?: defaultColor
                        val vibrant = palette?.getVibrantColor(dominant) ?: dominant
                        val muted = palette?.getMutedColor(dominant) ?: dominant

                        val c1 = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(vibrant, Color.BLACK, blendRatio), alpha)
                        val c2 = ColorUtils.setAlphaComponent(ColorUtils.blendARGB(muted, Color.BLACK, blendRatio), alpha)

                        val gradient = GradientDrawable(GradientDrawable.Orientation.entries[gradientType], intArrayOf(c1, c2))

                        gradient.cornerRadius = 0f
                        background = gradient
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // no-op
                }
            })
    }
}