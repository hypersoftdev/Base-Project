package com.hypersoft.baseproject.presentation.language.intent

sealed class LanguageIntent {
    object LoadLanguages : LanguageIntent()
    data class SelectLanguage(val languageCode: String) : LanguageIntent()
    object ApplyLanguage : LanguageIntent()
}