package com.hypersoft.baseproject

import android.app.Application
import com.hypersoft.baseproject.di.setup.DIComponent
import com.hypersoft.baseproject.di.setup.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.startKoin
import org.koin.core.lazyModules

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    @OptIn(KoinExperimentalAPI::class)
    private fun initKoin() {
        val koinModules = KoinModules()
        startKoin {
            androidContext(this@MainApplication)
            modules(koinModules.mainModuleList)
            //lazyModules(koinModules.backgroundModuleList)
        }

        //Start this observer if you don't want to get any callback in viewmodel
      //  startNetworkObserver()

    }


    private fun startNetworkObserver ()
    {
        val diComponent = DIComponent()
        diComponent.networkUseCase.startListening()
    }

    private fun stopNetworkObserver ()
    {
        val diComponent = DIComponent()
        diComponent.networkUseCase.stopListening()
    }


    override fun onTerminate() {
        stopNetworkObserver()
        super.onTerminate()
    }

}