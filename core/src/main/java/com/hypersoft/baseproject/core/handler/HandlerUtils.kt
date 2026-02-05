package com.hypersoft.baseproject.core.handler

import android.os.Handler
import android.os.Looper

/**
 * Runs [block] on the main thread after [delayMillis] delay.
 */
fun withDelay(delayMillis: Long = 300, block: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(block, delayMillis)
}