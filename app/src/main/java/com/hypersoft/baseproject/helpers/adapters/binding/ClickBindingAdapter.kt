package com.hypersoft.baseproject.helpers.adapters.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.hypersoft.baseproject.commons.listeners.DebounceListener.setDebounceClickListener

@BindingAdapter("debounceClick")
fun setDebouncedClick(view: View, debounceClick: () -> Unit) {
    view.setDebounceClickListener {
        debounceClick.invoke()
    }
}