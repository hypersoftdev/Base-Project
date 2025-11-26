package com.hypersoft.baseproject.presentation.media.di

import com.hypersoft.baseproject.presentation.media.viewModel.MediaViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaPresentationModule = lazyModule {
    viewModel { MediaViewModel() }
}