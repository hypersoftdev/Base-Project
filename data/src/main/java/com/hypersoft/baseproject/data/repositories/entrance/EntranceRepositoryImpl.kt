package com.hypersoft.baseproject.data.repositories.entrance

import com.hypersoft.baseproject.data.dataSources.storage.SharedPrefManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntranceRepositoryImpl(
    private val sharedPrefManager: SharedPrefManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : EntranceRepository {

    override suspend fun isFirstTime(): Boolean = withContext(ioDispatcher) {
        sharedPrefManager.isFirstTime
    }
}