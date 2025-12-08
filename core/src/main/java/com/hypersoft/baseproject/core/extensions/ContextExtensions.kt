package com.hypersoft.baseproject.core.extensions

import android.app.Activity
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import com.hypersoft.baseproject.core.R
import com.hypersoft.baseproject.core.constants.Constants.TAG
import com.hypersoft.baseproject.core.firebase.FirebaseUtils.recordException

/* ================================================================================================
 *  TOASTS
 * ================================================================================================ */

fun Context?.showToast(message: String?, long: Boolean = false) {
    if (this == null || message.isNullOrEmpty()) return
    Toast.makeText(this, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Context?.showToast(@StringRes resId: Int, long: Boolean = false) {
    this?.getString(resId)?.let { showToast(it, long) }
}

/* ================================================================================================
 *  SNACK_BARS
 * ================================================================================================ */

fun Context?.showSnackBar(
    view: View?,
    message: String?,
    anchorView: View? = null,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    if (this == null || view == null || message.isNullOrEmpty()) return
    Snackbar.make(view, message, duration).apply {
        this.anchorView = anchorView
        show()
    }
}

fun Context?.showSnackBar(
    view: View?,
    @StringRes resId: Int,
    anchorView: View? = null,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    this?.showSnackBar(view, getResString(resId), anchorView, duration)
}

fun Context?.showSnackBar(
    @StringRes messageResId: Int,
    @StringRes actionResId: Int,
    duration: Int = Snackbar.LENGTH_LONG,
    listener: View.OnClickListener?
) {
    (this as? Activity)?.findViewById<View>(android.R.id.content)?.let { root ->
        Snackbar.make(root, messageResId, duration)
            .setAction(actionResId, listener)
            .show()
    }
}

/* ================================================================================================
 *  RESOURCES
 * ================================================================================================ */

fun Context?.getResString(@StringRes resId: Int): String = this?.getString(resId) ?: ""

fun Context?.getAttrColor(attrId: Int): Int = this?.let {
    val typedValue = TypedValue()
    it.theme?.resolveAttribute(attrId, typedValue, true)
    typedValue.data
} ?: Color.BLACK

fun Context?.getResColor(@ColorRes colorId: Int): Int =
    this?.let { ContextCompat.getColor(it, colorId) } ?: Color.BLACK

fun Context?.getColorStateList(@ColorRes colorId: Int): ColorStateList? =
    this?.let { ColorStateList.valueOf(ContextCompat.getColor(it, colorId)) }

fun Context?.getDrawableResource(@DrawableRes drawableId: Int): Drawable? =
    this?.let { ContextCompat.getDrawable(it, drawableId) }

fun Context?.isDarkThemeActive(): Boolean {
    val nightMode = this?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
    return nightMode == Configuration.UI_MODE_NIGHT_YES
}

/* ================================================================================================
 *  INTENTS & ACTIONS
 * ================================================================================================ */

fun Context?.openWebUrl(url: String?) {
    if (this == null || url.isNullOrBlank()) return
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (ex: Exception) {
        ex.recordException("openWebUrl")
        Log.e(TAG, "openWebUrl failed", ex)
    }
}

fun Context?.openWebUrl(@StringRes resId: Int) = openWebUrl(getResString(resId))

fun Context?.openPlayStoreApp(packageName: String? = this?.packageName) {
    openWebUrl("https://play.google.com/store/apps/details?id=$packageName")
}

fun Context?.openSubscriptions() {
    if (this == null) return
    try {
        val url = "https://play.google.com/store/account/subscriptions?package=$packageName"
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (ex: Exception) {
        Log.e(TAG, "openSubscriptions failed", ex)
    }
}

fun Context?.openEmailApp(
    email: String?,
    subject: String = "",
    text: String = ""
) {
    if (this == null || email.isNullOrEmpty()) return

    try {
        // Gmail-specific intent
        val gmailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            setPackage("com.google.android.gm")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Check if Gmail exists
        if (gmailIntent.resolveActivity(packageManager) != null) {
            startActivity(gmailIntent)
        } else {
            // Fallback: generic chooser if Gmail isn’t installed
            val fallbackIntent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(Intent.createChooser(fallbackIntent, "Send mail..."))
        }

    } catch (ex: Exception) {
        Log.e(TAG, "openEmailApp failed", ex)
    }
}

fun Context?.openEmailApp(@StringRes emailResId: Int) = openEmailApp(getResString(emailResId))

fun Context?.shareApp() {
    if (this == null) return
    try {
        val url = "https://play.google.com/store/apps/details?id=$packageName"
        val appName = getResString(R.string.app_name)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, appName)
            putExtra(Intent.EXTRA_TEXT, url)
        }
        startActivity(Intent.createChooser(sendIntent, "Share App"))
    } catch (ex: Exception) {
        Log.e(TAG, "shareApp failed", ex)
    }
}

fun Context?.shareTextData(text: String?) {
    if (this == null || text.isNullOrEmpty()) return
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Sharing data...")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    } catch (ex: Exception) {
        Log.e(TAG, "shareTextData failed", ex)
    }
}

fun Context?.copyToClipboard(text: String?) {
    if (this == null || text.isNullOrEmpty()) {
        showToast("No text found")
        return
    }
    try {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("Copied Text", text))
        showToast("Copied to clipboard")
    } catch (ex: Exception) {
        Log.e(TAG, "copyToClipboard failed", ex)
    }
}

fun Context?.searchData(text: String?) {
    if (this == null || text.isNullOrEmpty()) return
    try {
        startActivity(Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, text)
        })
    } catch (ex: Exception) {
        Log.e(TAG, "searchData failed", ex)
    }
}

fun Context?.translateText(text: String?) {
    if (this == null || text.isNullOrEmpty()) return
    val url = "https://translate.google.com/#view=home&op=translate&sl=auto&tl=en&text=$text"
    openWebUrl(url)
}

fun Context?.openMirrorCastSettings(): Boolean {
    if (this == null) return false
    return try {
        startActivity(Intent("android.settings.CAST_SETTINGS"))
        true
    } catch (ex: Exception) {
        ex.recordException("openMirrorCastSettings")
        Log.e(TAG, "openMirrorCastSettings failed", ex)
        false
    }
}

/* ================================================================================================
 *  KEYBOARD & INPUT
 * ================================================================================================ */

fun Context?.hideKeyboard() {
    if (this == null) return
    try {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        val view = (this as? Activity)?.currentFocus ?: View(this)
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (ex: Exception) {
        Log.e(TAG, "hideKeyboard failed", ex)
    }
}