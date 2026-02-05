package tv.remote.control.roku.remoteforroku.core.transformer

import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * Page transformer that creates a smooth crossfade effect between pages.
 * The current page fades out while the next page fades in, creating a seamless transition.
 */
class CrossfadePageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                // Page is completely off-screen to the left
                position < -1f -> {
                    alpha = 0f
                }
                // Page is moving from left to center (entering)
                position <= 0f -> {
                    // Fade in: 0 -> 1 as position goes from -1 to 0
                    alpha = 1f + position
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                // Page is moving from center to right (exiting)
                position <= 1f -> {
                    // Fade out: 1 -> 0 as position goes from 0 to 1
                    alpha = 1f - position
                    translationX = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                // Page is completely off-screen to the right
                else -> {
                    alpha = 0f
                }
            }
        }
    }
}