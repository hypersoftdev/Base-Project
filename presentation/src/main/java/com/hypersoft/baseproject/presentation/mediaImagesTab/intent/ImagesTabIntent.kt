package com.hypersoft.baseproject.presentation.mediaImagesTab.intent

sealed class ImagesTabIntent {
    object LoadImages : ImagesTabIntent()
    data class ImageClicked(val imageUri: String) : ImagesTabIntent()
}