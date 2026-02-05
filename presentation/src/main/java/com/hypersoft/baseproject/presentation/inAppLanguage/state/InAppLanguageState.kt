package com.hypersoft.baseproject.presentation.inAppLanguage.state

import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language

data class InAppLanguageState(
    val isLoading: Boolean = false,
    val languages: List<Language> = emptyList(),
    val selectedLanguageCode: String? = null
)