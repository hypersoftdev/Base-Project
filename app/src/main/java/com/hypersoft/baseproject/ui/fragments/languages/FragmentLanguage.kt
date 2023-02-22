package com.hypersoft.baseproject.ui.fragments.languages

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentLanguageBinding
import com.hypersoft.baseproject.helpers.adapters.recyclerView.AdapterLanguage
import com.hypersoft.baseproject.helpers.dataModels.LanguageItem
import com.hypersoft.baseproject.helpers.dataProvider.DpLanguages
import com.hypersoft.baseproject.helpers.interfaces.OnLanguageItemClickListener
import com.hypersoft.baseproject.helpers.listeners.DebounceListener.setDebounceClickListener
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentLanguage : BaseFragment<FragmentLanguageBinding>(R.layout.fragment_language), OnLanguageItemClickListener {

    private val adapterLanguage by lazy { AdapterLanguage(this) }
    private val dpLanguages by lazy { DpLanguages() }
    private var languageItem: LanguageItem? = null

    override fun onViewCreatedOneTime() {
        initRecyclerView()
        fillList()

        binding.mbSubmitLanguage.setDebounceClickListener { onSubmitClick() }
    }

    override fun onViewCreatedEverytime() {}

    private fun initRecyclerView() {
        binding.rvListLanguage.adapter = adapterLanguage
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
            globalActivity.recreate()
        }
        popFrom(R.id.fragmentLanguage)
    }

    override fun navIconBackPressed() {
        onBackPressed()
    }

    override fun onBackPressed() {
        popFrom(R.id.fragmentLanguage)
    }
}