package com.hypersoft.baseproject.presentation.history.effect

sealed class HistoryEffect {
    data class ShowError(val message: String) : HistoryEffect()
}