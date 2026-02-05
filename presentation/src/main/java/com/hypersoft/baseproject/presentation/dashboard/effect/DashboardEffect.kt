package com.hypersoft.baseproject.presentation.dashboard.effect

sealed class DashboardEffect {
    object RegisterBackPress : DashboardEffect()
    data class ShowError(val message: String) : DashboardEffect()
}