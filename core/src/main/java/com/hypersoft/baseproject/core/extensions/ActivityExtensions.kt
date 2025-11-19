package com.hypersoft.baseproject.core.extensions

import android.app.Activity
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.hypersoft.baseproject.core.firebase.FirebaseUtils.recordException


/* ---------------------------------------------- BackPress ---------------------------------------------- */

fun AppCompatActivity.onBackPressedDispatcher(callback: () -> Unit) {
    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
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