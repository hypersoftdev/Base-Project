package com.hypersoft.baseproject.presentation.premium.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hypersoft.baseproject.presentation.premium.effect.PremiumEffect
import com.hypersoft.baseproject.presentation.premium.intent.PremiumIntent
import com.hypersoft.baseproject.presentation.premium.state.PremiumState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PremiumViewModel : ViewModel() {

    private val _state = MutableStateFlow(PremiumState())
    val state: StateFlow<PremiumState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PremiumEffect>()
    val effect: SharedFlow<PremiumEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: PremiumIntent) = viewModelScope.launch {
        when (intent) {
            is PremiumIntent.NavigateBack -> _effect.emit(PremiumEffect.NavigateBack)
        }
    }
}