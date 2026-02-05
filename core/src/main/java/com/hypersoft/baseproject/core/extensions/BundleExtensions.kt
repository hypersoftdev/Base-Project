package com.hypersoft.baseproject.core.extensions

import android.os.Build
import android.os.Bundle
import android.os.Parcelable

inline fun <reified T : Parcelable> Bundle?.getArgument(key: String): T? {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            this?.getParcelable(key, T::class.java)
        }

        else -> {
            @Suppress("DEPRECATION")
            this?.getParcelable(key)
        }
    }
}