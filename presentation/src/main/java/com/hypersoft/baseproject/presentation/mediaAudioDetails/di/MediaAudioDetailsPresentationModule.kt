package com.hypersoft.baseproject.presentation.mediaAudioDetails.di

import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaAudioDetailsPresentationModule = lazyModule {
    // ViewModel uses AndroidViewModel with Application and GetAudiosUseCase
    viewModel { MediaAudioDetailViewModel(androidApplication(), get()) }
}