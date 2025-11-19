package com.hypersoft.baseproject.core.di

import com.hypersoft.baseproject.core.observers.GeneralObserver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DIComponent : KoinComponent {
    val generalObserver by inject<GeneralObserver>()
}