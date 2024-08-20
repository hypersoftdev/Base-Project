package com.hypersoft.baseproject.app.flows.dashboard

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.databinding.FragmentDashboardBinding
import com.hypersoft.baseproject.utilities.extensions.navigateTo

class FragmentDashboard : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private val navController by lazy { (childFragmentManager.findFragmentById(binding.fcvContainerDashboard.id) as NavHostFragment).navController }

    override fun onViewCreated() {
        initBottomNav()
        initObserver()
    }

    private fun initBottomNav() {
        binding.bnvContainerDashboard.setupWithNavController(navController)
        binding.bnvContainerDashboard.setOnItemSelectedListener { it.onNavDestinationSelected(navController) }
    }

    private fun initObserver() {
        diComponent.generalObserver.navDashboardLiveData.observe(viewLifecycleOwner) {
            navigateTo(R.id.fragmentDashboard, it)
        }
        diComponent.generalObserver.navDashboardDirectionLiveData.observe(viewLifecycleOwner) {
            navigateTo(R.id.fragmentDashboard, it)
        }
    }

}