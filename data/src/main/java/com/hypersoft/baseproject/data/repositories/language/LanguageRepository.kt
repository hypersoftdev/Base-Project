package com.hypersoft.baseproject.data.repositories.language

import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    suspend fun getLanguages(): Flow<List<Language>>
    suspend fun updateLanguageCode(selectedCode: String): Flow<List<Language>>
    suspend fun applyLanguage(selectedCode: String)
}