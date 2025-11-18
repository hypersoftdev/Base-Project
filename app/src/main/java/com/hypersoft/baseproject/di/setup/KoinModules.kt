package com.hypersoft.baseproject.di.setup

import com.hypersoft.baseproject.app.features.remoteConfig.data.datasource.DataSourceNetwork
import com.hypersoft.baseproject.app.features.remoteConfig.data.datasource.DataSourceRemoteConfig
import com.hypersoft.baseproject.app.features.remoteConfig.data.repositories.RepositoryNetworkImpl
import com.hypersoft.baseproject.app.features.remoteConfig.data.repositories.RepositoryRemoteConfigImpl
import com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase.UseCaseNetwork
import com.hypersoft.baseproject.app.features.remoteConfig.domain.usecase.UseCaseRemoteConfig
import com.hypersoft.baseproject.app.features.remoteConfig.presentation.viewmodel.ViewModelNetwork
import com.hypersoft.baseproject.di.domain.manager.InternetManager
import com.hypersoft.baseproject.di.domain.observers.GeneralObserver
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule
import org.koin.dsl.module

class KoinModules {

    /* ________________________________ Main Thread Modules ________________________________ */

    private val mainModules = module {
        single { InternetManager(androidContext()) }
        single { GeneralObserver() }
    }

    /* ________________________________ Background Thread Modules ________________________________ */

    private val networkModule = lazyModule {
        single { DataSourceNetwork(androidContext()) }
        single { DataSourceRemoteConfig() }

        single { RepositoryNetworkImpl(get()) }
        single { RepositoryRemoteConfigImpl(get()) }

        single { UseCaseNetwork(get()) }
        single { UseCaseRemoteConfig(get()) }

        viewModel { ViewModelNetwork(get(), get()) }
    }

    /* ________________________________ Modules List ________________________________ */

    val mainModuleList = listOf(mainModules)
    val backgroundModuleList = listOf(networkModule)
}