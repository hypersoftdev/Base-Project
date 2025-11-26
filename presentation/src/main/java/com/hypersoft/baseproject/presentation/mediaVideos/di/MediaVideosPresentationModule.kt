package com.hypersoft.baseproject.presentation.mediaVideos.di

import com.hypersoft.baseproject.presentation.mediaVideos.viewModel.MediaVideosViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaVideosPresentationModule = lazyModule {
    viewModel { MediaVideosViewModel(get(), get()) }
}