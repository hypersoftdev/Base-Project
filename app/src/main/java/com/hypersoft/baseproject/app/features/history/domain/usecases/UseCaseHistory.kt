package com.hypersoft.baseproject.app.features.history.domain.usecases

import com.hypersoft.baseproject.app.features.history.data.repository.RepositoryHistory
import com.hypersoft.baseproject.app.features.history.domain.entities.ItemHistory

class UseCaseHistory(private val repositoryHistory: RepositoryHistory) {

    fun fetchHistory(): List<ItemHistory> {
        // Business Logic
        val allHistories = repositoryHistory.getHistory()
        return allHistories
    }
}