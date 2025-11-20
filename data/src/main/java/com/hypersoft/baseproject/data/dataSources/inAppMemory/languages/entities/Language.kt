package com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities

import androidx.annotation.DrawableRes

data class Language(
    @param:DrawableRes val flagIcon: Int,
    val languageCode: String,
    val languageName: String,
    val isSelected: Boolean = false,
    val itemClick: () -> Unit = {}
)