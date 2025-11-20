package com.hypersoft.baseproject.presentation.mediaImages.intent

sealed class MediaImagesIntent {
    object LoadFolders : MediaImagesIntent()
    data class ImageClicked(val imageUri: String) : MediaImagesIntent()
}