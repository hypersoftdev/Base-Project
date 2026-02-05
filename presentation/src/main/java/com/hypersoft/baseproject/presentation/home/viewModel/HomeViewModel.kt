package com.hypersoft.baseproject.presentation.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.home.effect.HomeEffect
import com.hypersoft.baseproject.presentation.home.intent.HomeIntent
import com.hypersoft.baseproject.presentation.home.state.HomeState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect: SharedFlow<HomeEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: HomeIntent) = viewModelScope.launch {
        when (intent) {
            is HomeIntent.DrawerClicked -> _effect.emit(HomeEffect.NavigateToDrawer)
            is HomeIntent.PremiumClicked -> _effect.emit(HomeEffect.NavigateToPremium)
            is HomeIntent.MediaClicked -> _effect.emit(HomeEffect.NavigateToMedia)
        }
    }
}