package com.hypersoft.baseproject.presentation.mediaImages.state

import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.presentation.mediaImages.enums.MediaImagesPermissionLevel

data class MediaImagesState(
    val isLoading: Boolean = false,
    val folders: List<ImageFolderEntity> = emptyList(),
    val permission: MediaImagesPermissionLevel = MediaImagesPermissionLevel.Idle
)