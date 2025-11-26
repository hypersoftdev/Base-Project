package com.hypersoft.baseproject.presentation.mediaImagesTab.di

import com.hypersoft.baseproject.domain.media.useCases.GetImagesUseCase
import com.hypersoft.baseproject.presentation.mediaImagesTab.viewModel.ImagesTabViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.lazyModule

val imagesTabPresentationModule = lazyModule {
    viewModel { (folderName: String) -> ImagesTabViewModel(getImagesUseCase = get<GetImagesUseCase>(), folderName = folderName) }
}