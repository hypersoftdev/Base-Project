package com.hypersoft.baseproject.utilities.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

/**
 * Created by: Sohaib Ahmed
 * Date: 2/6/2025
 *
 * Links:
 * - LinkedIn: https://linkedin.com/in/epegasus
 * - GitHub: https://github.com/epegasus
 */

fun View.gone() {
    this.visibility = View.GONE
}

fun View.remove() {
    (parent as? ViewGroup)?.removeView(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.goneIf(shouldGone: Boolean) {
    if (shouldGone) {
        this.visibility = View.GONE
    }
}

fun View.visibleIf(shouldVisible: Boolean) {
    if (shouldVisible) {
        this.visibility = View.VISIBLE
    }
}

fun View.getBitmap(): Bitmap? {
    try {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    } catch (ex: Exception) {
        //ex.recordException("ViewExtensions")
        return null
    }
}

fun View.setMargins(
    left: Int = this.marginLeft,
    top: Int = this.marginTop,
    right: Int = this.marginRight,
    bottom: Int = this.marginBottom,
) {
    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
        setMargins(left, top, right, bottom)
    }
}

fun View.animateContentChange(
    updateAction: () -> Unit,
    fadeDuration: Long = 600L,
    interpolator: Interpolator = AccelerateDecelerateInterpolator(),
    onAnimationEnd: (() -> Unit)? = null
) {
    // Fade out
    this.animate()
        .alpha(0f)
        .setDuration(fadeDuration)
        .setInterpolator(interpolator)
        .withEndAction {
            updateAction() // Update content after fade-out

            // Fade in
            this.animate()
                .alpha(1f)
                .setDuration(fadeDuration)
                .setInterpolator(interpolator)
                .withEndAction {
                    onAnimationEnd?.invoke()
                }
                .start()
        }
        .start()
}