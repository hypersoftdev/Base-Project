package com.hypersoft.baseproject.presentation.language.di

import com.hypersoft.baseproject.presentation.language.viewModel.LanguageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val languagePresentationModule = lazyModule {
    viewModel { LanguageViewModel(get(), get()) }
}