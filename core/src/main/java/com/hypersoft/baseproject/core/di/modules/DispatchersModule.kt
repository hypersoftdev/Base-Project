package com.hypersoft.baseproject.core.di.modules

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.lazyModule

val dispatchersModule = lazyModule {
    single { Dispatchers.IO }
    single { Dispatchers.Default }
}