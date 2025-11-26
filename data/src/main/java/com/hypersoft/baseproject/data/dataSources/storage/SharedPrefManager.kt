package com.hypersoft.baseproject.data.dataSources.storage

import android.content.Context
import androidx.core.content.edit

private const val KEY_IS_FIRST_TIME = "isFirstTime"

class SharedPrefManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var isFirstTime: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_IS_FIRST_TIME, value) }
}