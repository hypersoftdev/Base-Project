package com.hypersoft.baseproject.app.flows.home.presentation.ui.dialog

import com.hypersoft.baseproject.databinding.DialogExitBinding
import com.hypersoft.baseproject.utilities.base.dialog.BaseDialog

class DialogExit : BaseDialog<DialogExitBinding>(DialogExitBinding::inflate) {

    private var exitListener: (() -> Unit)? = null

    fun setListener(callback: () -> Unit) {
        exitListener = callback
    }

    override fun onDialogCreated() {
        binding.mbCancelExit.setOnClickListener { dismiss() }
        binding.mbExitExit.setOnClickListener { onExitClick() }
    }

    private fun onExitClick() {
        dismiss()
        exitListener?.invoke()
    }
}