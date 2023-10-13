package com.hypersoft.baseproject.helpers.adapters.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.hypersoft.baseproject.commons.listeners.RapidSafeListener.setOnRapidClickSafeListener

@BindingAdapter("rapidSafeClick")
fun setOnRapidSafeClick(view: View, rapidSafeClick: () -> Unit) {
    view.setOnRapidClickSafeListener {
        rapidSafeClick.invoke()
    }
}