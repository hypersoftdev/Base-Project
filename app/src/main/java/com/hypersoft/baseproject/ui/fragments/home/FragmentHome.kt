package com.hypersoft.baseproject.ui.fragments.home

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentHomeBinding
import com.hypersoft.baseproject.helpers.firebase.EventsProvider
import com.hypersoft.baseproject.helpers.firebase.FirebaseUtils.postFirebaseEvent
import com.hypersoft.baseproject.helpers.listeners.DebounceListener.setDebounceClickListener
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentHome : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun onViewCreatedOneTime() {
        binding.btnGalleryScreen.setDebounceClickListener { onGalleryClick() }
        binding.btnRoomDb.setDebounceClickListener { onRoomDbClick() }
        binding.btnResultScreen.setDebounceClickListener { onResultClick() }
        binding.btnPermission.setDebounceClickListener { onPermissionClick() }

        EventsProvider.HOME_SCREEN.postFirebaseEvent()
    }

    override fun onViewCreatedEverytime() {
        // For ViewModels
    }

    private fun onRoomDbClick() {
        navigateTo(R.id.fragmentHome, R.id.action_fragmentHome_to_fragmentEnlistCountry)
    }

    private fun onGalleryClick() {
        navigateTo(R.id.fragmentHome, R.id.action_fragmentHome_to_fragmentPictures)
    }

    private fun onResultClick() {
        navigateTo(R.id.fragmentHome, R.id.action_fragmentHome_to_fragmentSampleResult)
    }

    private fun onPermissionClick() {
        navigateTo(R.id.fragmentHome, R.id.action_fragmentHome_to_fragmentPermissions)
    }

    override fun navIconBackPressed() {
        mainActivity.openDrawer()
    }

    override fun onBackPressed() {
        mainActivity.homeBackPressed()
    }
}