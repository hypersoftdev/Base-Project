package com.hypersoft.baseproject.presentation.mediaAudioDetails.di

import com.hypersoft.baseproject.presentation.mediaAudioDetails.factory.MediaPlayerFactory
import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaAudioDetailsPresentationModule = lazyModule {
    factory { MediaPlayerFactory(androidContext()) }
    viewModel { MediaAudioDetailViewModel(get(), get()) }
}