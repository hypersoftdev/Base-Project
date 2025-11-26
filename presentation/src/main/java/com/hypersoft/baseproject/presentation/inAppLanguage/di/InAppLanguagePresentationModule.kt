package com.hypersoft.baseproject.presentation.inAppLanguage.di

import com.hypersoft.baseproject.presentation.inAppLanguage.viewModel.InAppLanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val inAppLanguagePresentationModule = lazyModule {
    viewModel { InAppLanguageViewModel(get(), get()) }
}