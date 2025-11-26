package com.hypersoft.baseproject.data.repositories.history

import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.entities.History

interface HistoryRepository {
    suspend fun getHistories(): List<History>
}