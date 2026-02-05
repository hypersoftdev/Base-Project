package com.hypersoft.baseproject.presentation.base.activity

import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.hypersoft.baseproject.core.base.activity.ParentActivity
import com.hypersoft.baseproject.core.di.DIComponent

abstract class BaseActivity<T : ViewBinding>(bindingFactory: (LayoutInflater) -> T) : ParentActivity<T>(bindingFactory) {
    protected val diComponent by lazy { DIComponent() }
}