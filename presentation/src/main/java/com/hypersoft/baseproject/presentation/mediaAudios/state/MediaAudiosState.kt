package com.hypersoft.baseproject.presentation.mediaAudios.state

import com.hypersoft.baseproject.domain.media.entities.AudioEntity

data class MediaAudiosState(
    val isLoading: Boolean = false,
    val audios: List<AudioEntity> = emptyList()
)