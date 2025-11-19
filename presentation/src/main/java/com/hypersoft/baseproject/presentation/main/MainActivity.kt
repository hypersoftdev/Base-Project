package com.hypersoft.baseproject.presentation.main

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hypersoft.baseproject.core.base.activity.BaseActivity
import com.hypersoft.baseproject.core.extensions.onBackPressedDispatcher
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.ActivityMainBinding

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
                R.id.entranceFragment,
                R.id.languageFragment,
                    -> {
                }

                else -> navController.popBackStack()
            }
        }
    }

    private val destinationChangeListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.entranceFragment,
            R.id.premiumFragment,
            R.id.drawerFragment,
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