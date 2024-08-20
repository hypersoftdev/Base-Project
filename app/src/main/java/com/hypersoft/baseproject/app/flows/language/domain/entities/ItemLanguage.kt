package com.hypersoft.baseproject.app.flows.language.domain.entities

data class ItemLanguage(
    val flagIcon: Int,
    val languageCode: String,
    val languageName: String,
    var isSelected: Boolean = false,
)