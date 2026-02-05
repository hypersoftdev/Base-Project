package com.hypersoft.baseproject.presentation.language.intent

sealed class LanguageIntent {
    object LoadLanguages : LanguageIntent()
    object ApplyLanguage : LanguageIntent()
    data class SelectLanguage(val languageCode: String) : LanguageIntent()
}