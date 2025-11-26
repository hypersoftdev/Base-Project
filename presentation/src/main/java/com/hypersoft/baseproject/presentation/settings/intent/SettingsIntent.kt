package com.hypersoft.baseproject.presentation.settings.intent

sealed class SettingsIntent {
    data class LoadVersionName(val versionName: String) : SettingsIntent()
    object LanguageClicked : SettingsIntent()
    object FeedbackClicked : SettingsIntent()
    object RateUsClicked : SettingsIntent()
    object ShareAppClicked : SettingsIntent()
    object PrivacyPolicyClicked : SettingsIntent()
}