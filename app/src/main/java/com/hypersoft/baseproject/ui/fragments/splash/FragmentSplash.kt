package com.hypersoft.baseproject.ui.fragments.splash

import androidx.lifecycle.lifecycleScope
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSplashBinding
import com.hypersoft.baseproject.ui.activities.SplashActivity
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentSplash : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun onViewCreatedOneTime() {
        fetchRemoteConfiguration()
    }

    override fun onViewCreatedEverytime() {}

    override fun navIconBackPressed() {}

    override fun onBackPressed() {}

    private fun fetchRemoteConfiguration() {
        diComponent.remoteConfiguration.checkRemoteConfig {
            withDelay(2000) {
                lifecycleScope.launchWhenResumed {
                    (activity as SplashActivity).nextActivity()
                }
            }
        }
    }

}