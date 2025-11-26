package com.hypersoft.baseproject.presentation.history.intent

sealed class HistoryIntent {
    object LoadHistories : HistoryIntent()
}