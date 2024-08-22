package com.hypersoft.baseproject.app.flows.home.presentation.ui.fragment

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.app.flows.home.presentation.ui.dialog.DialogExit
import com.hypersoft.baseproject.databinding.FragmentHomeBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.onBackPressedDispatcher
import com.hypersoft.baseproject.utilities.extensions.showSafe

class FragmentHome : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val dialogExit by lazy { DialogExit() }
    private var isDialogShown = false

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
        dialogExit.setListener {
            activity?.finish()
        }

        dialogExit.showSafe(this, isDialogShown) {
            isDialogShown = it
        }
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