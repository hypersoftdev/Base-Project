package com.hypersoft.baseproject.presentation.mediaImages.effect

sealed class MediaImagesEffect {
    object NavigateBack : MediaImagesEffect()
    object GrantPermissionClick : MediaImagesEffect()
    data class NavigateToDetail(val imageUri: String) : MediaImagesEffect()
    data class ShowError(val message: String) : MediaImagesEffect()
}