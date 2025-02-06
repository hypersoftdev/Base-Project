package com.hypersoft.baseproject.app.flows.remoteconfig.presentation.ui

import com.hypersoft.baseproject.databinding.FragmentRemoteConfigBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.showToast

class FragmentRemoteConfig:BaseFragment<FragmentRemoteConfigBinding>(FragmentRemoteConfigBinding::inflate) {

    override fun onViewCreated() {

        diComponent.networkViewModel.remoteConfigStatus.observeForever {

            requireContext().showToast("Remote Config fetch: ${it}")
        }
    }
}