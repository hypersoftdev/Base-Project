package com.hypersoft.baseproject.core.observers

import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections

class GeneralObserver {

    private val _navigateById = SingleLiveEvent<Int>()
    val navigateById: LiveData<Int> get() = _navigateById

    private val _navigateByDirections = SingleLiveEvent<NavDirections>()
    val navigateByDirections: LiveData<NavDirections> get() = _navigateByDirections

    val _navigationDirectionsMediaImageLiveData = SingleLiveEvent<NavDirections>()
    val navigationDirectionsMediaImageLiveData: LiveData<NavDirections> get() = _navigationDirectionsMediaImageLiveData

    fun navigate(destination: Int) {
        _navigateById.value = destination
    }

    fun navigate(navDirections: NavDirections) {
        _navigateByDirections.value = navDirections
    }
}