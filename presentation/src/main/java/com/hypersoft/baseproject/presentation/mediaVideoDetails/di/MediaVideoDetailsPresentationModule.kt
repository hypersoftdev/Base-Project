package com.hypersoft.baseproject.presentation.mediaVideoDetails.di

import com.hypersoft.baseproject.presentation.mediaVideoDetails.factory.MediaPlayerFactory
import com.hypersoft.baseproject.presentation.mediaVideoDetails.viewModel.MediaVideoDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaVideoDetailsPresentationModule = lazyModule {
    factory { MediaPlayerFactory(androidContext()) }
    viewModel { MediaVideoDetailViewModel(get(), get()) }
}