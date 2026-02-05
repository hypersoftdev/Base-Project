package com.hypersoft.baseproject.presentation.home.ui.dialog

import com.hypersoft.baseproject.presentation.base.dialog.BaseDialog
import com.hypersoft.baseproject.presentation.databinding.DialogExitBinding

class ExitDialog : BaseDialog<DialogExitBinding>(DialogExitBinding::inflate) {

    private var exitListener: (() -> Unit)? = null

    fun setListener(callback: () -> Unit) {
        exitListener = callback
    }

    override fun onDialogCreated() {
        binding.mbCancelExit.setOnClickListener { dismiss() }
        binding.mbExitExit.setOnClickListener { onExitClick() }
    }

    private fun onExitClick() {
        exitListener?.invoke()
        dismiss()
    }
}