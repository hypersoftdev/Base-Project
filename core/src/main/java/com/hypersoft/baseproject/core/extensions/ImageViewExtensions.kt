package com.hypersoft.baseproject.core.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
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