package com.hypersoft.baseproject.di.modules

import com.hypersoft.baseproject.core.info.AppInfoProvider
import com.hypersoft.baseproject.info.AppInfoProviderImpl
import org.koin.dsl.lazyModule

val appModule = lazyModule {
    single<AppInfoProvider> { AppInfoProviderImpl() }
}