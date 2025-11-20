package com.hypersoft.baseproject.presentation.inAppLanguage.intent

sealed class InAppLanguageIntent {
    object LoadLanguages : InAppLanguageIntent()
    object NavigateBack : InAppLanguageIntent()
    data class SelectLanguage(val languageCode: String) : InAppLanguageIntent()
    object ApplyLanguage : InAppLanguageIntent()
}