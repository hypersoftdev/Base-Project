package com.hypersoft.baseproject.data.repositories.history

import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.HistoryDataSource
import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.entities.History
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepositoryImpl(
    private val dataSource: HistoryDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HistoryRepository {

    override suspend fun getHistories(): List<History> = withContext(ioDispatcher) {
        dataSource.getHistories()
    }
}