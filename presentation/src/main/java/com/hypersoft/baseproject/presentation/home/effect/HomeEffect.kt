package com.hypersoft.baseproject.presentation.home.effect

sealed class HomeEffect {
    object NavigateToDrawer : HomeEffect()
    object NavigateToPremium : HomeEffect()
    object NavigateToMedia : HomeEffect()
}