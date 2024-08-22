package com.hypersoft.baseproject.utilities.extensions

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.hypersoft.baseproject.utilities.base.dialog.ParentDialogDismissal
import com.hypersoft.baseproject.utilities.utils.ConstantUtils.TAG

fun DialogFragment.onBackPressedDispatcher(callback: () -> Unit) {
    activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            callback.invoke()
        }
    })
}

/**
 * @param isDialogShown:  Create a global variable in fragment to check if the dialog fragment is shown or not.
 */
fun ParentDialogDismissal.showSafe(fragment: Fragment?, isDialogShown: Boolean, callback: (Boolean) -> Unit) {
    if (this.isAdded || fragment == null || !fragment.isAdded || fragment.childFragmentManager.findFragmentByTag(tag) != null || isDialogShown) {
        Log.e(TAG, "DialogFragment: showSafe: not added")
        return
    }

    show(fragment.childFragmentManager, tag)
    this.dismissCallback = { callback.invoke(false) }
    callback.invoke(true)
}

/**
 * @param isDialogShown:  Create a global variable in fragment to check if the dialog fragment is shown or not.
 */
fun ParentDialogDismissal.dismissSafe(fragment: Fragment?, isDialogShown: Boolean) {
    launchWhenResumed {
        try {
            dismiss()
        } catch (ex: Exception) {
            Log.e(TAG, "dismissSafe: ", ex)
        }
    }
}