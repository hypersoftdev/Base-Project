package com.hypersoft.baseproject.presentation.mediaImages.state

import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity

data class MediaImagesState(
    val isLoading: Boolean = false,
    val folders: List<ImageFolderEntity> = emptyList(),
    val error: String? = null
)