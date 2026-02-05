package com.hypersoft.baseproject.presentation.home.intent

sealed class HomeIntent {
    object DrawerClicked : HomeIntent()
    object PremiumClicked : HomeIntent()
    object MediaClicked : HomeIntent()
}