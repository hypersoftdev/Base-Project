package com.hypersoft.baseproject.ui.fragments.splash

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSplashLanguageBinding
import com.hypersoft.baseproject.helpers.adapters.listView.AdapterLanguage
import com.hypersoft.baseproject.helpers.dataModels.LanguageItem
import com.hypersoft.baseproject.helpers.dataProvider.DpLanguages
import com.hypersoft.baseproject.ui.activities.SplashActivity
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentSplashLanguage : BaseFragment<FragmentSplashLanguageBinding>(R.layout.fragment_splash_language) {

    private val dpLanguages by lazy { DpLanguages() }
    private var languageItem: LanguageItem? = null
    private val adapterLanguage by lazy {
        AdapterLanguage(
            globalContext,
            dpLanguages.getLanguagesList(diComponent.sharedPreferenceUtils.selectedLanguageCode)
        )
    }

    override fun onViewCreatedOneTime() {
        initLanguages()

        binding.mbContinueLanguage.setOnClickListener { onContinueClick() }
    }

    override fun onViewCreatedEverytime() {}

    private fun initLanguages() = binding.actDropDownLanguage.apply {
        languageItem = dpLanguages.getLanguagesList()[0].also {
            setText(it.languageName, false)
        }
        setAdapter(adapterLanguage)
        setOnItemClickListener { parent, view, position, id ->
            languageItem = dpLanguages.getLanguagesList()[position].also {
                setText(it.languageName, false)
            }
        }
    }

    /**
     * Add Service in Manifest first
     */

    private fun onContinueClick() {
        languageItem?.let {
            diComponent.sharedPreferenceUtils.selectedLanguageCode = it.languageCode
            diComponent.sharedPreferenceUtils.showFirstScreen = false
            (activity as SplashActivity).nextActivity()
        }
    }

    override fun navIconBackPressed() {}

    override fun onBackPressed() {}
}