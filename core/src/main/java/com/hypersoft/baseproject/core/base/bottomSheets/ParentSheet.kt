package com.hypersoft.baseproject.core.base.bottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ParentSheet<T : ViewBinding>(private val bindingFactory: (LayoutInflater) -> T) : ParentSheetDismissal() {

    /**
     * These properties are only valid between onCreateView and onDestroyView
     * @property binding
     *          -> after onCreateView
     *          -> before onDestroyView
     */
    private var _binding: T? = null
    protected val binding: T get() = _binding ?: throw IllegalStateException("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = bindingFactory(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSheetCreated()
        initObservers()
    }

    abstract fun onSheetCreated()

    open fun initObservers() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}