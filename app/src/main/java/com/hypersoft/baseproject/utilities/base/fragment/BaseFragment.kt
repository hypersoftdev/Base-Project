package com.hypersoft.baseproject.utilities.base.fragment

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.app.flows.entrance.presentation.ui.MainActivity
import com.hypersoft.baseproject.di.setup.DIComponent

abstract class BaseFragment<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentFragment<T>(bindingFactory) {

    protected val diComponent by lazy { DIComponent() }

    protected fun setStatusBarPadding() {
        val padding = (activity as? MainActivity)?.statusBarHeight ?: 0
        binding.root.setPadding(0, padding, 0, 0)
    }
}