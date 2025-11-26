package com.hypersoft.baseproject.presentation.home.ui.fragment

import com.hypersoft.baseproject.core.base.dialog.safeShow
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.onBackPressedDispatcher
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentHomeBinding
import com.hypersoft.baseproject.presentation.home.effect.HomeEffect
import com.hypersoft.baseproject.presentation.home.intent.HomeIntent
import com.hypersoft.baseproject.presentation.home.state.HomeState
import com.hypersoft.baseproject.presentation.home.ui.dialog.ExitDialog
import com.hypersoft.baseproject.presentation.home.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel: HomeViewModel by viewModel()

    override fun onViewCreated() {
        binding.mbDrawerHome.setOnClickListener { viewModel.handleIntent(HomeIntent.DrawerClicked) }
        binding.mbPremiumHome.setOnClickListener { viewModel.handleIntent(HomeIntent.PremiumClicked) }
        binding.mbMediaHome.setOnClickListener { viewModel.handleIntent(HomeIntent.MediaClicked) }
    }

    override fun onResume() {
        super.onResume()
        registerBackPress()
    }

    override fun initObservers() {
        observeState()
        observeEffect()
    }

    private fun registerBackPress() {
        onBackPressedDispatcher { viewModel.handleIntent(HomeIntent.BackPressed) }
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: HomeState) {
        // No UI state updates needed currently
    }

    private fun handleEffect(effect: HomeEffect) {
        when (effect) {
            is HomeEffect.ShowExitDialog -> showExitDialog()
            is HomeEffect.NavigateToDrawer -> diComponent.generalObserver.navigate(R.id.action_dashboardFragment_to_drawerFragment)
            is HomeEffect.NavigateToPremium -> diComponent.generalObserver.navigate(R.id.action_global_premiumFragment)
            is HomeEffect.NavigateToMedia -> diComponent.generalObserver.navigate(R.id.action_dashboardFragment_to_nav_graph_media)
        }
    }

    private fun showExitDialog() {
        val dialog = ExitDialog()
        dialog.setListener { activity?.finish() }
        safeShow(dialog, "ExitDialog")
    }
}