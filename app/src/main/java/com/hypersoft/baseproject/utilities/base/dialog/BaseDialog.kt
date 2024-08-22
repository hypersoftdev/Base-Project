package com.hypersoft.baseproject.utilities.base.dialog

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.di.setup.DIComponent

abstract class BaseDialog<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentDialog<T>(bindingFactory) {

    protected val diComponent by lazy { DIComponent() }

}