package com.hypersoft.baseproject.app.flows.language.domain.usecases

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hypersoft.baseproject.app.flows.language.data.repository.RepositoryLanguage
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage
import com.hypersoft.baseproject.utilities.utils.ConstantUtils.TAG

class UseCaseLanguage(private val repositoryLanguage: RepositoryLanguage) {

    private var defaultLanguageCode: String? = null
    private var userSelectedCode: String? = null

    fun fetchLanguages(appliedLanguageCode: String): List<ItemLanguage> {
        // Business Logic
        val allLanguages = repositoryLanguage.getLanguages()

        val selectedLanguage = if (appliedLanguageCode.isNotEmpty()) {
            allLanguages.find { it.languageCode == appliedLanguageCode }
        } else {
            null
        } ?: allLanguages.find { it.languageCode == "en" }

        selectedLanguage?.isSelected = true
        defaultLanguageCode = selectedLanguage?.languageCode

        return allLanguages
    }

    fun updateLanguages(selectedCode: String): List<ItemLanguage> {
        userSelectedCode = selectedCode
        val allLanguages = repositoryLanguage.getLanguages()
        allLanguages.find { it.languageCode == selectedCode }?.isSelected = true
        return allLanguages
    }

    fun applyLanguage() : Boolean {
        // If none/same language is being applied
        if (defaultLanguageCode == userSelectedCode) {
            return false
        }
        // If no language is being applied
        if (userSelectedCode.isNullOrEmpty()) {
            return false
        }
        Log.d(TAG, "applyLanguage: $userSelectedCode")
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(userSelectedCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
        return true
    }
}