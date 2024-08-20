package com.hypersoft.baseproject.app.flows.settings

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSettingsBinding
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.openEmailApp
import com.hypersoft.baseproject.utilities.extensions.openPlayStoreApp
import com.hypersoft.baseproject.utilities.extensions.openWebUrl
import com.hypersoft.baseproject.utilities.extensions.shareApp

class FragmentSettings : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun onViewCreated() {
        binding.mtvAppLanguageSettings.setOnClickListener { navigateInAppLanguage() }
        binding.mtvRateUsSettings.setOnClickListener { context.openPlayStoreApp() }
        binding.mtvShareAppSettings.setOnClickListener { context.shareApp() }
        binding.mtvFeedbackSettings.setOnClickListener { context.openEmailApp(R.string.app_email) }
        binding.mtvPrivacyPolicySettings.setOnClickListener { context.openWebUrl(R.string.app_privacy_policy_link) }
    }

    private fun navigateInAppLanguage() {
        diComponent.generalObserver._navDashboardLiveData.value = R.id.action_fragmentDashboard_to_fragmentInAppLanguage
    }
}