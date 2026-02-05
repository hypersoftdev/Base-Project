package com.hypersoft.baseproject.presentation.dashboard.ui

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.hypersoft.baseproject.core.base.dialog.safeShow
import com.hypersoft.baseproject.core.extensions.collectWhenCreated
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.onBackPressedDispatcher
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.base.fragment.BaseFragment
import com.hypersoft.baseproject.presentation.dashboard.effect.DashboardEffect
import com.hypersoft.baseproject.presentation.dashboard.intent.DashboardIntent
import com.hypersoft.baseproject.presentation.dashboard.state.DashboardState
import com.hypersoft.baseproject.presentation.dashboard.viewModel.DashboardViewModel
import com.hypersoft.baseproject.presentation.databinding.FragmentDashboardBinding
import com.hypersoft.baseproject.presentation.home.ui.dialog.ExitDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private val viewModel: DashboardViewModel by viewModel()
    private val navController by lazy { (childFragmentManager.findFragmentById(binding.fcvContainerDashboard.id) as NavHostFragment).navController }

    override fun onViewCreated() {
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        binding.bnvContainerDashboard.setupWithNavController(navController)
        binding.bnvContainerDashboard.setOnItemSelectedListener { it.onNavDestinationSelected(navController) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleIntent(DashboardIntent.RegisterBackPress)
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { renderState(it) }
        collectWhenCreated(viewModel.effect) { handleEffect(it) }
        observeNavigation()
    }

    private fun renderState(state: DashboardState) {}

    private fun handleEffect(effect: DashboardEffect) {
        when (effect) {
            is DashboardEffect.RegisterBackPress -> registerBackPress()
            is DashboardEffect.ShowError -> context.showToast(effect.message)
        }
    }

    /**
     *  This needed to be done to fix issue occur in following flow in real applications
     *      Flow: Dashboard > Home > Setting > InAppLanguage > Update Language > Setting > BackPress (kills app instead of going back to home)
     */
    private fun registerBackPress() {
        onBackPressedDispatcher {
            when (navController.currentDestination?.id == R.id.homeFragment) {
                true -> showExitDialog()
                false -> navController.popBackStack()
            }
        }
    }

    private fun observeNavigation() {
        diComponent.generalObserver.navigateById.observe(viewLifecycleOwner) { navigateTo(R.id.dashboardFragment, it) }
        diComponent.generalObserver.navigateByDirections.observe(viewLifecycleOwner) { navigateTo(R.id.dashboardFragment, it) }
    }

    private fun showExitDialog() {
        val dialog = ExitDialog()
        dialog.setListener { activity?.finish() }
        safeShow(dialog, "ExitDialog")
    }
}