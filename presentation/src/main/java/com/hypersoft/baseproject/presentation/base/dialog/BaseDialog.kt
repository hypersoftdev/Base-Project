package com.hypersoft.baseproject.presentation.base.dialog

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.core.base.dialog.ParentDialog
import com.hypersoft.baseproject.core.di.DIComponent

abstract class BaseDialog<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentDialog<T>(bindingFactory) {
    protected val diComponent by lazy { DIComponent() }
}