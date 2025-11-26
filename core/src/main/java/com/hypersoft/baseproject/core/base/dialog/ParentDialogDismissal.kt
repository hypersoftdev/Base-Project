package com.hypersoft.baseproject.core.base.dialog

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

open class ParentDialogDismissal : DialogFragment() {

    var onDismissCallback: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }
}

fun AppCompatActivity.safeShow(dialog: DialogFragment, tag: String) {
    if (!supportFragmentManager.isStateSaved) {
        dialog.show(supportFragmentManager, tag)
    }
}

fun Fragment.safeShow(dialog: DialogFragment, tag: String) {
    if (isAdded && !childFragmentManager.isStateSaved) {
        dialog.show(childFragmentManager, tag)
    }
}

fun DialogFragment.safeDismiss() {
    if (isAdded) {
        dismissAllowingStateLoss()
    }
}