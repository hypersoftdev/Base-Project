package com.hypersoft.baseproject.presentation.inAppLanguage.intent

sealed class InAppLanguageIntent {
    object LoadLanguages : InAppLanguageIntent()
    object NavigateBack : InAppLanguageIntent()
    object ApplyLanguage : InAppLanguageIntent()
    data class SelectLanguage(val languageCode: String) : InAppLanguageIntent()
}