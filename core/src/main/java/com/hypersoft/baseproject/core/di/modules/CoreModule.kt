package com.hypersoft.baseproject.core.di.modules

import com.hypersoft.baseproject.core.network.InternetManager
import com.hypersoft.baseproject.core.observers.GeneralObserver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val coreModule = lazyModule {
    single { InternetManager(androidContext()) }
    single { GeneralObserver() }
}