package com.hypersoft.baseproject.presentation.history.state

import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.entities.History

data class HistoryState(
    val isLoading: Boolean = false,
    val histories: List<History> = emptyList()
)