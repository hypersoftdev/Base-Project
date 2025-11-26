package com.hypersoft.baseproject.presentation.dashboard

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentDashboardBinding

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private val navController by lazy { (childFragmentManager.findFragmentById(binding.fcvContainerDashboard.id) as NavHostFragment).navController }

    override fun onViewCreated() {
        binding.bnvContainerDashboard.setupWithNavController(navController)
        binding.bnvContainerDashboard.setOnItemSelectedListener { it.onNavDestinationSelected(navController) }
    }

    override fun initObservers() {
        observeNavigation()
    }

    private fun observeNavigation() {
        diComponent.generalObserver.navigateById.observe(viewLifecycleOwner) { navigateTo(R.id.dashboardFragment, it) }
        diComponent.generalObserver.navigateByDirections.observe(viewLifecycleOwner) { navigateTo(R.id.dashboardFragment, it) }
    }
}