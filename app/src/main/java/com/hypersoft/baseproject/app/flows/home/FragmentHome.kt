package com.hypersoft.baseproject.app.flows.home

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentHomeBinding
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.onBackPressedDispatcher
import com.hypersoft.baseproject.utilities.extensions.showToast

class FragmentHome : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun onViewCreated() {
        registerBackPress()

        binding.mbDrawerHome.setOnClickListener { navigateDrawer() }
        binding.mbPremiumHome.setOnClickListener { navigatePremium() }
        binding.mbMediaHome.setOnClickListener { navigateMedia() }
    }

    private fun registerBackPress() {
        onBackPressedDispatcher { showExitDialog() }
    }

    private fun showExitDialog() {
        context.showToast("Oye Hoye")
    }

    private fun navigateDrawer() {
        diComponent.generalObserver._navDashboardLiveData.value = R.id.action_fragmentDashboard_to_fragmentDrawer
    }

    private fun navigatePremium() {
        diComponent.generalObserver._navDashboardLiveData.value = R.id.action_global_fragmentPremium
    }

    private fun navigateMedia() {
        diComponent.generalObserver._navDashboardLiveData.value = R.id.action_fragmentDashboard_to_nav_graph_media
    }
}