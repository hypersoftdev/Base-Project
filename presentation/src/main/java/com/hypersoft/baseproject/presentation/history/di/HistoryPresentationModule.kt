package com.hypersoft.baseproject.presentation.history.di

import com.hypersoft.baseproject.presentation.history.viewModel.HistoryViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val historyPresentationModule = lazyModule {
    viewModel { HistoryViewModel(get()) }
}