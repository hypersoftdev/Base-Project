package com.hypersoft.baseproject.presentation.home.di

import com.hypersoft.baseproject.presentation.home.viewModel.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val homePresentationModule = lazyModule {
    viewModel { HomeViewModel() }
}