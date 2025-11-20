package com.hypersoft.baseproject.presentation.mediaAudios.intent

sealed class MediaAudiosIntent {
    object LoadAudios : MediaAudiosIntent()
    data class AudioClicked(val audioUri: String) : MediaAudiosIntent()
}