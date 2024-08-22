package com.hypersoft.baseproject.app.flows.entrance.presentation.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.ActivityMainBinding
import com.hypersoft.baseproject.utilities.base.activity.BaseActivity
import com.hypersoft.baseproject.utilities.extensions.onBackPressedDispatcher

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val navController by lazy { (supportFragmentManager.findFragmentById(binding.fcvContainerMain.id) as NavHostFragment).navController }

    override fun onPreCreated() {
        installSplashTheme()
        enableMaterialDynamicTheme()
        hideStatusBar(1)
    }

    override fun onCreated() {
        registerBackPress()

        navController.addOnDestinationChangedListener(destinationChangeListener)
    }

    private fun registerBackPress() {
        onBackPressedDispatcher {
            when (navController.currentDestination?.id) {
                R.id.fragmentEntrance,
                R.id.fragmentLanguage,
                -> {
                }

                else -> navController.popBackStack()
            }
        }
    }

    private val destinationChangeListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.fragmentEntrance,
            R.id.fragmentPremium,
            R.id.fragmentDrawer,
            -> {
                includeTopPadding = false
                includeBottomPadding = false
            }

            else -> {
                includeTopPadding = true
                includeBottomPadding = true
            }
        }
    }
}