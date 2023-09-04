package com.hypersoft.baseproject.ui.fragments.entrance

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.commons.dataProvider.DpLanguages
import com.hypersoft.baseproject.commons.interfaces.OnLanguageItemClickListener
import com.hypersoft.baseproject.commons.listeners.DebounceListener.setDebounceClickListener
import com.hypersoft.baseproject.commons.models.LanguageItem
import com.hypersoft.baseproject.databinding.FragmentEntranceLanguageBinding
import com.hypersoft.baseproject.helpers.adapters.recyclerView.AdapterLanguage
import com.hypersoft.baseproject.ui.activities.ActivityEntrance
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentEntranceLanguage : BaseFragment<FragmentEntranceLanguageBinding>(R.layout.fragment_entrance_language),
    OnLanguageItemClickListener {

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
        launchWhenResumed {
            diComponent.sharedPreferenceUtils.showFirstScreen = false
            (activity as ActivityEntrance).nextActivity()
        }
    }

    override fun navIconBackPressed() {}
    override fun onBackPressed() {}
}