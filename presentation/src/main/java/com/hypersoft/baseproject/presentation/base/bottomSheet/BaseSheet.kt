package com.hypersoft.baseproject.presentation.base.bottomSheet

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.core.base.bottomSheets.ParentSheet
import com.hypersoft.baseproject.core.di.DIComponent

abstract class BaseSheet<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentSheet<T>(bindingFactory) {
    protected val diComponent by lazy { DIComponent() }
}