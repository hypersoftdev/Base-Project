package com.hypersoft.baseproject.data.repositories.entrance

import com.hypersoft.baseproject.data.dataSources.storage.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntranceRepositoryImpl(private val sharedPrefManager: SharedPrefManager) : EntranceRepository {
    override suspend fun isFirstTime(): Boolean = withContext(Dispatchers.IO) {
        sharedPrefManager.isFirstTime
    }
}