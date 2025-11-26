package com.hypersoft.baseproject.presentation.mediaAudios.di

import com.hypersoft.baseproject.presentation.mediaAudios.viewModel.MediaAudiosViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaAudiosPresentationModule = lazyModule {
    viewModel { MediaAudiosViewModel(get(), get()) }
}