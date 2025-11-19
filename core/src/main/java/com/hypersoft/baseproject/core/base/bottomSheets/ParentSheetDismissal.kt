package com.hypersoft.baseproject.core.base.bottomSheets

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class ParentSheetDismissal : BottomSheetDialogFragment() {

    var onDismissCallback: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback?.invoke()
    }
}

fun AppCompatActivity.safeShow(dialog: BottomSheetDialogFragment, tag: String) {
    if (!supportFragmentManager.isStateSaved) {
        dialog.show(supportFragmentManager, tag)
    }
}

fun Fragment.safeShow(dialog: BottomSheetDialogFragment, tag: String) {
    if (isAdded && !childFragmentManager.isStateSaved) {
        dialog.show(childFragmentManager, tag)
    }
}

fun BottomSheetDialogFragment.safeDismiss() {
    if (isAdded) {
        dismissAllowingStateLoss()
    }
}