package com.hypersoft.baseproject.ui.fragments.splash

import androidx.lifecycle.lifecycleScope
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSplashStartBinding
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentSplashStart : BaseFragment<FragmentSplashStartBinding>(R.layout.fragment_splash_start) {

    override fun onViewCreatedOneTime() {
        withDelay(3000) {
            lifecycleScope.launchWhenResumed {
                navigateTo(R.id.fragmentStart, R.id.action_fragmentStart_to_fragmentLanguage)
            }
        }
    }

    override fun onViewCreatedEverytime() {}

    override fun navIconBackPressed() {}

    override fun onBackPressed() {}

}