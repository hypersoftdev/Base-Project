package com.hypersoft.baseproject.domain.media.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageFolderEntity(
    val folderName: String,
    val imageCount: Int = 0
) : Parcelable