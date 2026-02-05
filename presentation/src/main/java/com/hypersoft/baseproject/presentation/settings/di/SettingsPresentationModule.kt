package com.hypersoft.baseproject.presentation.settings.di

import com.hypersoft.baseproject.presentation.settings.viewModel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val settingsPresentationModule = lazyModule {
    viewModel { SettingsViewModel(get()) }
}