package com.hypersoft.baseproject

import android.app.Application
import com.hypersoft.baseproject.di.setup.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.lazyModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        val koinModules = KoinModules()
        startKoin {
            androidContext(this@MainApplication)
            modules(koinModules.mainModuleList)
            lazyModules(koinModules.backgroundModuleList)
        }
    }
}