package com.hypersoft.baseproject.domain.media.entities

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AudioEntity(
    val uri: Uri,
    val id: Long = 0,
    val displayName: String = "",
    val size: Long = 0,
    val duration: Long = 0,
    val artist: String = "",
    val album: String = "",
    val dateModified: Long = 0,
    val folderName: String = ""
) : Parcelable