package com.hypersoft.baseproject.presentation.settings.ui

import com.hypersoft.baseproject.core.extensions.collectWhenCreated
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.openEmailApp
import com.hypersoft.baseproject.core.extensions.openPlayStoreApp
import com.hypersoft.baseproject.core.extensions.openWebUrl
import com.hypersoft.baseproject.core.extensions.shareApp
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.base.fragment.BaseFragment
import com.hypersoft.baseproject.presentation.databinding.FragmentSettingsBinding
import com.hypersoft.baseproject.presentation.settings.effect.SettingsEffect
import com.hypersoft.baseproject.presentation.settings.intent.SettingsIntent
import com.hypersoft.baseproject.presentation.settings.state.SettingsState
import com.hypersoft.baseproject.presentation.settings.viewModel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated() {
        binding.mtvAppLanguageSettings.setOnClickListener { viewModel.handleIntent(SettingsIntent.LanguageClicked) }
        binding.mtvFeedbackSettings.setOnClickListener { viewModel.handleIntent(SettingsIntent.FeedbackClicked) }
        binding.mtvRateUsSettings.setOnClickListener { viewModel.handleIntent(SettingsIntent.RateUsClicked) }
        binding.mtvShareAppSettings.setOnClickListener { viewModel.handleIntent(SettingsIntent.ShareAppClicked) }
        binding.mtvPrivacyPolicySettings.setOnClickListener { viewModel.handleIntent(SettingsIntent.PrivacyPolicyClicked) }
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { renderState(it) }
        collectWhenCreated(viewModel.effect) { handleEffect(it) }
    }

    private fun renderState(state: SettingsState) {
        val text = getString(coreR.string.version_s, state.versionName)
        binding.mtvVersionSettings.text = text
    }

    private fun handleEffect(effect: SettingsEffect) {
        when (effect) {
            is SettingsEffect.NavigateToLanguage -> diComponent.generalObserver.navigate(R.id.action_dashboardFragment_to_inAppLanguageFragment)
            is SettingsEffect.GiveFeedback -> context.openEmailApp(coreR.string.app_email)
            is SettingsEffect.ShowRateUsDialog -> context.openPlayStoreApp()
            is SettingsEffect.ShareApp -> context.shareApp()
            is SettingsEffect.OpenPrivacyPolicy -> context.openWebUrl(coreR.string.app_privacy_policy_link)
            is SettingsEffect.ShowError -> context.showToast(effect.message)
        }
    }
}