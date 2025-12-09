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

/**
 * Loads album art for the given audio ID in a thread-safe manner.
 * MediaStore queries are performed on IO thread to avoid ANR.
 * Uses view tags to manage coroutine lifecycle and prevent leaks.
 */
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
 * Loads album art into an ImageView and applies a dynamic gradient background derived from
 * the album artwork using Palette. If artwork is missing or load fails, a fallback color and/or
 * placeholder/error drawable is used.
 *
 * Features:
 * - Extracts dominant, vibrant, and muted colors from album art.
 * - Blends extracted colors for smooth, readable gradients.
 * - Updates both the ImageView and the background view.
 * - Supports placeholder/error drawables.
 * - Safe handling of missing album IDs and missing artwork.
 *
 * @param backgroundView The view on which the gradient background will be applied.
 * @param audioId MediaStore audio ID used to query album art.
 * @param defaultColor The fallback background color when no artwork exists.
 * @param alpha Alpha value (0–255) for gradient colors.
 * @param gradientType Orientation for the gradient (TOP_BOTTOM, LEFT_RIGHT, etc.).
 * @param blendRatio How much to blend extracted colors with black (for contrast).
 * @param placeholder Drawable to show while loading (optional).
 * @param error Drawable to show if album art fails to load (optional).
 */
suspend fun ImageView.loadAlbumArtWithGradientBackground(
    backgroundView: View,
    audioId: Long?,
    defaultColor: Int = Color.BLACK,
    alpha: Int = 255,
    gradientType: GradientDrawable.Orientation = GradientDrawable.Orientation.TOP_BOTTOM,
    blendRatio: Float = 0.35f,
    @DrawableRes placeholder: Int? = null,
    @DrawableRes error: Int? = null
) {
    val ctx = context

    // --- Early termination when audioId is null ---
    if (audioId == null) {
        placeholder?.let { setImageResource(it) } ?: setImageDrawable(null)
        backgroundView.setBackgroundColor(defaultColor)
        return
    }

    // --- Query album ID (IO thread) ---
    val resolver = ctx.contentResolver
    val albumId = withContext(Dispatchers.IO) {
        resolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Media.ALBUM_ID),
            "${MediaStore.Audio.Media._ID} = ?",
            arrayOf(audioId.toString()),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            } else 0L
        } ?: 0L
    }

    // --- No album? fallback immediately ---
    if (albumId == 0L) {
        placeholder?.let { setImageResource(it) } ?: setImageDrawable(null)
        backgroundView.setBackgroundColor(defaultColor)
        return
    }

    // --- Build album art URI ---
    val albumArtUri = ContentUris.withAppendedId(
        "content://media/external/audio/albumart".toUri(),
        albumId
    )

    // --- Load image & derive gradient using Glide ---
    Glide.with(ctx)
        .asBitmap()
        .load(albumArtUri)
        .dontAnimate()
        .signature(ObjectKey("album-$albumId")) // force refresh per album
        .apply {
            placeholder?.let { placeholder(it) }
            error?.let { error(it) }
        }
        .into(object : CustomTarget<Bitmap>() {

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                // Set album art in the ImageView
                this@loadAlbumArtWithGradientBackground.setImageBitmap(resource)

                // Extract palette asynchronously
                Palette.from(resource).generate { palette ->
                    val dominant = palette?.getDominantColor(defaultColor) ?: defaultColor
                    val vibrant = palette?.getVibrantColor(dominant) ?: dominant
                    val muted = palette?.getMutedColor(dominant) ?: dominant

                    // Blend for readability
                    val c1 = ColorUtils.setAlphaComponent(
                        ColorUtils.blendARGB(vibrant, Color.BLACK, blendRatio),
                        alpha
                    )
                    val c2 = ColorUtils.setAlphaComponent(
                        ColorUtils.blendARGB(muted, Color.BLACK, blendRatio),
                        alpha
                    )

                    // Build gradient
                    backgroundView.background = GradientDrawable(
                        gradientType,
                        intArrayOf(c1, c2)
                    )
                }
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                // Set error image if provided
                if (error != null) {
                    this@loadAlbumArtWithGradientBackground.setImageResource(error)
                } else {
                    this@loadAlbumArtWithGradientBackground.setImageDrawable(null)
                }

                // Apply fallback background
                backgroundView.setBackgroundColor(defaultColor)
            }

            override fun onLoadCleared(placeholderDrawable: Drawable?) {
                // Display placeholder when Glide clears request
                if (placeholder != null) {
                    this@loadAlbumArtWithGradientBackground.setImageResource(placeholder)
                }
            }
        })
}