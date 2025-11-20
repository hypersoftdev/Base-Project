package com.hypersoft.baseproject.presentation.mediaImageDetails.di

import com.hypersoft.baseproject.presentation.mediaImageDetails.viewModel.MediaImageDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val mediaImageDetailsPresentationModule = lazyModule {
    viewModel { (imageUri: String) -> MediaImageDetailViewModel(imageUri = imageUri) }
}