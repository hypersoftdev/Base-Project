package com.hypersoft.baseproject.data.repositories.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.LanguageDataSource
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language
import com.hypersoft.baseproject.data.dataSources.storage.SharedPrefManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LanguageRepositoryImpl(
    private val dataSource: LanguageDataSource,
    private val sharedPrefManager: SharedPrefManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LanguageRepository {

    private val currentCode: String
        get() = AppCompatDelegate.getApplicationLocales()
            .toLanguageTags()
            .takeIf { it.isNotEmpty() }
            ?: "en"

    override suspend fun getLanguages(): Flow<List<Language>> {
        return getLanguagesByCode(currentCode)
    }

    override suspend fun updateLanguageCode(selectedCode: String): Flow<List<Language>> {
        return getLanguagesByCode(selectedCode)
    }

    override suspend fun applyLanguage(selectedCode: String) {
        withContext(ioDispatcher) {
            sharedPrefManager.isFirstTime = false
        }

        val localeList = LocaleListCompat.forLanguageTags(selectedCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun getLanguagesByCode(languageCode: String): Flow<List<Language>> = flow {
        val languages = dataSource.getLanguages().map { it.copy(isSelected = it.languageCode == languageCode) }
        emit(languages)
    }.flowOn(ioDispatcher)
}