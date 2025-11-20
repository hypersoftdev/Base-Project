package com.hypersoft.baseproject.presentation.premium.ui

import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentPremiumBinding
import com.hypersoft.baseproject.presentation.premium.effect.PremiumEffect
import com.hypersoft.baseproject.presentation.premium.intent.PremiumIntent
import com.hypersoft.baseproject.presentation.premium.state.PremiumState
import com.hypersoft.baseproject.presentation.premium.viewModel.PremiumViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PremiumFragment : BaseFragment<FragmentPremiumBinding>(FragmentPremiumBinding::inflate) {

    private val viewModel: PremiumViewModel by viewModel()

    override fun onViewCreated() {
        binding.mbBackPremium.setOnClickListener { viewModel.handleIntent(PremiumIntent.NavigateBack) }
    }

    override fun initObservers() {
        observeState()
        observeEffects()
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun observeEffects() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: PremiumState) {
        // No UI state updates needed currently
    }

    private fun handleEffect(effect: PremiumEffect) {
        when (effect) {
            is PremiumEffect.NavigateBack -> popFrom(R.id.premiumFragment)
        }
    }
}