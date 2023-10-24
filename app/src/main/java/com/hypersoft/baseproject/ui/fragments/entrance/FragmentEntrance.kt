package com.hypersoft.baseproject.ui.fragments.entrance

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentEntranceBinding
import com.hypersoft.baseproject.ui.activities.ActivityEntrance
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentEntrance : BaseFragment<FragmentEntranceBinding>(R.layout.fragment_entrance) {

    override fun onViewCreatedOneTime() {
        fetchRemoteConfiguration()
        withDelay(3000) {
            launchWhenResumed {
                (activity as ActivityEntrance).nextActivity()
            }
        }
    }

    override fun onViewCreatedEverytime() {}

    override fun navIconBackPressed() {}

    override fun onBackPressed() {}

    private fun fetchRemoteConfiguration() {
        diComponent.remoteConfiguration.checkRemoteConfig {
         // do your work here
        }
    }

}