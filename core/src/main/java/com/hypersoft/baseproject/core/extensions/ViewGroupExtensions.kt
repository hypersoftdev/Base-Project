package com.hypersoft.baseproject.core.extensions

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.hypersoft.baseproject.core.constants.Constants.TAG

fun ViewGroup.addCleanView(view: View?) {
    if (view == null) {
        Log.e(TAG, "addCleanView: View ref is null")
        return
    }
    (view.parent as? ViewGroup)?.removeView(view)
    this.removeAllViews()
    view.let { this.addView(it) }
    this.visible()
}