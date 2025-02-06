package com.hypersoft.baseproject.app.features.remoteConfig.presentation.ui

import android.util.Log
import com.hypersoft.baseproject.databinding.FragmentRemoteConfigBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.remove
import com.hypersoft.baseproject.utilities.utils.ConstantUtils.TAG

class FragmentRemoteConfig : BaseFragment<FragmentRemoteConfigBinding>(FragmentRemoteConfigBinding::inflate) {

    override fun onViewCreated() {
        diComponent.networkViewModel.remoteSuccessLiveData.observe(viewLifecycleOwner) {
            val text = "Remote Config fetch: $it"
            updateUI(text)
        }
        diComponent.networkViewModel.errorLiveData.observe(viewLifecycleOwner) {
            val text = "Remote Config fetch: $it"
            updateUI(text)
        }
    }

    private fun updateUI(text: String) {
        Log.d(TAG, "onViewCreated: $text")
        binding.mtvTitleRemoteConfig.text = text
        binding.progressBarRemoteConfig.remove()
    }
}