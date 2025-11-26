package com.hypersoft.baseproject.domain.media.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageEntity(
    val uri: Uri,
    val id: Long = 0,
    val displayName: String = "",
    val size: Long = 0,
    val width: Int = 0,
    val height: Int = 0,
    val dateModified: Long = 0,
    val folderName: String = ""
) : Parcelable