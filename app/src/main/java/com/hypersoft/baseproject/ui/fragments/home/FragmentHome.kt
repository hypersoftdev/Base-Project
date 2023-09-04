package com.hypersoft.baseproject.ui.fragments.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentHomeBinding
import com.hypersoft.baseproject.helpers.firebase.EventsProvider
import com.hypersoft.baseproject.helpers.firebase.FirebaseUtils.postFirebaseEvent
import com.hypersoft.baseproject.commons.listeners.DebounceListener.setDebounceClickListener
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentHome : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override fun onViewCreatedOneTime() {
        binding.btnResultScreen.setDebounceClickListener { onResultClick() }
        binding.btnPermission.setDebounceClickListener { onPermissionClick() }

        EventsProvider.HOME_SCREEN.postFirebaseEvent()
    }

    override fun onViewCreatedEverytime() {
        initToolbarMenu()
    }

    private fun initToolbarMenu() {
        val menuHost = globalActivity as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_premium, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_item_premium -> showToast("Here is Premium")
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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