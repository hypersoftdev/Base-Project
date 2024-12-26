package com.hypersoft.baseproject.di.setup

import com.hypersoft.baseproject.app.flows.remoteconfig.data.datasource.NetworkStatusDataSource
import com.hypersoft.baseproject.app.flows.remoteconfig.data.repository.NetworkRepository
import com.hypersoft.baseproject.app.flows.remoteconfig.domain.usecase.NetworkUseCase
import com.hypersoft.baseproject.app.flows.remoteconfig.presentation.viewmodel.NetworkViewModel
import com.hypersoft.baseproject.di.domain.manager.InternetManager
import com.hypersoft.baseproject.di.domain.observers.GeneralObserver
import com.hypersoft.baseproject.utilities.dummyconfig.RemoteConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.lazyModule
import org.koin.dsl.module

class KoinModules {

    /* ________________________________ Main Thread Modules ________________________________ */

    private val mainModules = module {
        single { InternetManager(androidContext()) }
        single { GeneralObserver() }
        single { RemoteConfig() }

        single { NetworkStatusDataSource(get()) }
        single { NetworkRepository() }
        single { NetworkUseCase(get(),get()) }
        single { NetworkViewModel(get()) }
    }

    /* ________________________________ Background Thread Modules ________________________________ */

    @OptIn(KoinExperimentalAPI::class)
    private val domainModules = lazyModule {

    }

    /* ________________________________ Modules List ________________________________ */

    val mainModuleList = listOf(mainModules)
    val backgroundModuleList = listOf(domainModules)

}