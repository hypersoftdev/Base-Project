package com.hypersoft.baseproject.presentation.mediaImagesTab.state

import com.hypersoft.baseproject.domain.media.entities.ImageEntity

data class ImagesTabState(
    val isLoading: Boolean = false,
    val images: List<ImageEntity> = emptyList()
)