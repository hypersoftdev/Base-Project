package com.hypersoft.baseproject.core.extensions

import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.graphics.createBitmap
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.gone() {
    if (visibility != View.GONE) {
        this.visibility = View.GONE
    }
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        this.visibility = View.VISIBLE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        this.visibility = View.INVISIBLE
    }
}

fun View.remove() {
    (parent as? ViewGroup)?.removeView(this)
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
        val bitmap = createBitmap(width, height)
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


fun View.startSmoothShakeLoop(
    distanceDp: Float = 12f,
    shakes: Float = 2f,      // number of shakes per burst
    shakeDuration: Long = 600,
    pauseDuration: Long = 1500
) {
    val distancePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        distanceDp,
        resources.displayMetrics
    )

    val handler = Handler(Looper.getMainLooper())

    fun startShake() {
        val animator = ObjectAnimator.ofFloat(
            this,
            View.TRANSLATION_X,
            0f,
            distancePx
        ).apply {
            interpolator = CycleInterpolator(shakes)
            duration = shakeDuration
        }

        animator.start()

        handler.postDelayed(
            { startShake() },
            shakeDuration + pauseDuration
        )
    }

    startShake()
}

fun View.startInfiniteShake(
    distanceDp: Float = 6f,
    duration: Long = 120
): ObjectAnimator {

    val distancePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        distanceDp,
        resources.displayMetrics
    )

    return ObjectAnimator.ofFloat(
        this,
        View.TRANSLATION_X,
        -distancePx,
        distancePx
    ).apply {
        interpolator = LinearInterpolator()
        this.duration = duration
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
        start()
    }
}

fun View.stopShake() {
    animate().cancel()
    translationX = 0f
}