package com.hypersoft.baseproject.ui.fragments.splash

import androidx.lifecycle.lifecycleScope
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentSplashLanguageBinding
import com.hypersoft.baseproject.helpers.adapters.recyclerView.AdapterLanguage
import com.hypersoft.baseproject.helpers.dataModels.LanguageItem
import com.hypersoft.baseproject.helpers.dataProvider.DpLanguages
import com.hypersoft.baseproject.helpers.interfaces.OnLanguageItemClickListener
import com.hypersoft.baseproject.helpers.listeners.DebounceListener.setDebounceClickListener
import com.hypersoft.baseproject.ui.activities.SplashActivity
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentSplashLanguage : BaseFragment<FragmentSplashLanguageBinding>(R.layout.fragment_splash_language), OnLanguageItemClickListener {

    private val adapterLanguage by lazy { AdapterLanguage(this) }
    private val dpLanguages by lazy { DpLanguages() }
    private var languageItem: LanguageItem? = null

    override fun onViewCreatedOneTime() {
        initRecyclerView()
        fillList()

        binding.btnContinue.setDebounceClickListener { onSubmitClick() }
    }

    override fun onViewCreatedEverytime() {}

    private fun initRecyclerView() {
        binding.langRecyclerview.adapter = adapterLanguage
    }

    private fun fillList() {
        val list = dpLanguages.getLanguagesList(diComponent.sharedPreferenceUtils.selectedLanguageCode)
        adapterLanguage.submitList(list)
    }

    override fun onItemClick(languageItem: LanguageItem) {
        this.languageItem = languageItem
        val newList = dpLanguages.getLanguagesList(languageItem.languageCode)
        adapterLanguage.submitList(newList)
    }

    private fun onSubmitClick() {
        languageItem?.let {
            diComponent.sharedPreferenceUtils.selectedLanguageCode = it.languageCode
        }
        lifecycleScope.launchWhenResumed {
            diComponent.sharedPreferenceUtils.showFirstScreen = false
            (activity as SplashActivity).nextActivity()
        }
    }

    override fun navIconBackPressed() {}
    override fun onBackPressed() {}
}