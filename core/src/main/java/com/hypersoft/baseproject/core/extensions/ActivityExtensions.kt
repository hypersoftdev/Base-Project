package com.hypersoft.baseproject.core.extensions

import android.app.Activity
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hypersoft.baseproject.core.firebase.FirebaseUtils.recordException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/* ---------------------------------------------- BackPress ---------------------------------------------- */

fun AppCompatActivity.onBackPressedDispatcher(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
}

/* ---------------------------------------------- Collectors ---------------------------------------------- */

inline fun AppCompatActivity.repeatWhen(lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, crossinline block: suspend () -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}

inline fun <T> AppCompatActivity.collectWhenCreated(flow: Flow<T>, crossinline block: suspend (T) -> Unit) {
    repeatWhen(lifecycleState = Lifecycle.State.CREATED) { flow.collect { block(it) } }
}

inline fun <T> AppCompatActivity.collectWhenStarted(flow: Flow<T>, crossinline block: suspend (T) -> Unit) {
    repeatWhen(lifecycleState = Lifecycle.State.STARTED) { flow.collect { block(it) } }
}

/* ---------------------------------------------- Keyboards ---------------------------------------------- */

fun Activity?.hideKeyboard() {
    this?.let {
        try {
            val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view: IBinder? = findViewById<View?>(android.R.id.content)?.windowToken
            inputMethodManager.hideSoftInputFromWindow(view, 0)
        } catch (ex: Exception) {
            ex.recordException("ActivityExtensions > hideKeyboard")
        }
    }
}

fun Activity?.showKeyboard(view: View) {
    this?.let {
        try {
            val inputMethodManager: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } catch (ex: Exception) {
            ex.recordException("ActivityExtensions > showKeyboard")
        }
    }
}