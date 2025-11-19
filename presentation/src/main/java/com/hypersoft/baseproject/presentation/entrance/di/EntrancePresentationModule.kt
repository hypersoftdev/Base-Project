package com.hypersoft.baseproject.presentation.entrance.di

import com.hypersoft.baseproject.presentation.entrance.viewModel.EntranceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val entrancePresentationModule = lazyModule {
    viewModel { EntranceViewModel(get()) }
}