package com.hypersoft.baseproject.presentation.language.state

import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language

data class LanguageState(
    val isLoading: Boolean = false,
    val languages: List<Language> = emptyList(),
    val selectedLanguageCode: String? = null
)