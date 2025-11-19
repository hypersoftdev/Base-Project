package com.hypersoft.baseproject.core.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class ParentDialog<T : ViewBinding>(private val bindingInflater: (LayoutInflater) -> T) : ParentDialogDismissal() {

    /**
     * These properties are only valid between onCreateView and onDestroyView
     * @property binding
     *          -> after onCreateView
     *          -> before onDestroyView
     */
    private var _binding: T? = null
    protected val binding: T get() = _binding ?: throw IllegalStateException("Binding accessed after onDestroyView")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = bindingInflater(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        dialog.setOnShowListener { onDialogCreated() }

        return dialog
    }

    /**
     * Setup your dialog UI here after it's shown.
     */
    protected open fun onDialogCreated() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}