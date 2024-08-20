package com.hypersoft.baseproject.app.features.history.data.repository

import com.hypersoft.baseproject.app.features.history.data.datasources.DpHistory
import com.hypersoft.baseproject.app.features.history.domain.entities.ItemHistory

class RepositoryHistory {

    fun getHistory(): List<ItemHistory> {
        val dpHistory = DpHistory()
        return dpHistory.historyList
    }
}