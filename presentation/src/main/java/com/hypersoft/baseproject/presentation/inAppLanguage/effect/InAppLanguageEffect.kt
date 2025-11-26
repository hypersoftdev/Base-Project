package com.hypersoft.baseproject.presentation.inAppLanguage.effect

sealed class InAppLanguageEffect {
    object NavigateBack : InAppLanguageEffect()
    data class ShowError(val message: String) : InAppLanguageEffect()
}