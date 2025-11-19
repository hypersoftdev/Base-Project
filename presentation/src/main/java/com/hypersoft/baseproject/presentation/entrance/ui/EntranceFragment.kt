package com.hypersoft.baseproject.presentation.entrance.ui

import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentEntranceBinding
import com.hypersoft.baseproject.presentation.entrance.effect.EntranceEffect
import com.hypersoft.baseproject.presentation.entrance.state.EntranceState
import com.hypersoft.baseproject.presentation.entrance.viewModel.EntranceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EntranceFragment : BaseFragment<FragmentEntranceBinding>(FragmentEntranceBinding::inflate) {

    private val viewModel: EntranceViewModel by viewModel()

    override fun onViewCreated() {}

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

    private fun renderState(state: EntranceState) {
        when (state.isLoading) {
            true -> binding.lavAnimationEntrance.resumeAnimation()
            false -> binding.lavAnimationEntrance.pauseAnimation()
        }
    }

    private fun handleEffect(effect: EntranceEffect) {
        when (effect) {
            is EntranceEffect.NavigateToLanguage -> navigateTo(R.id.entranceFragment, R.id.action_entranceFragment_to_languageFragment)
            is EntranceEffect.NavigateToDashboard -> navigateTo(R.id.entranceFragment, R.id.action_entranceFragment_to_dashboardFragment)
            is EntranceEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}