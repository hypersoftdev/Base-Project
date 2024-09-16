package com.hypersoft.baseproject.app.features.media.presentation.images.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hypersoft.baseproject.app.features.media.domain.images.useCases.UseCaseMediaImage

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class ViewModelMediaImageProvider(private val useCaseMediaImage: UseCaseMediaImage) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelMediaImage::class.java)) {
            return ViewModelMediaImage(useCaseMediaImage) as T
        }
        return super.create(modelClass)
    }
}