package com.hypersoft.baseproject.ui.fragments.languages

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.commons.dataProvider.DpLanguages
import com.hypersoft.baseproject.commons.models.LanguageItem
import com.hypersoft.baseproject.databinding.FragmentLanguageBinding
import com.hypersoft.baseproject.helpers.adapters.listView.AdapterLanguage
import com.hypersoft.baseproject.ui.activities.MainActivity
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentLanguage : BaseFragment<FragmentLanguageBinding>(R.layout.fragment_language) {

    private val dpLanguages by lazy { DpLanguages() }
    private var languageItem: LanguageItem? = null
    private var adapterLanguage:AdapterLanguage? = null
    private val langList =  dpLanguages.getLanguagesList(diComponent.sharedPreferenceUtils.selectedLanguageCode)

    override fun onViewCreatedOneTime() {}

    override fun onViewCreatedEverytime() {
        initLanguages()
        binding.mbContinueLanguage.setOnClickListener { onContinueClick() }
    }

    private fun initLanguages() = binding.actDropDownLanguage.apply {
        adapterLanguage = AdapterLanguage(requireContext(),langList)
        val indexOf = langList.indexOfFirst{ it.languageCode == diComponent.sharedPreferenceUtils.selectedLanguageCode }
        languageItem = langList[indexOf].also {
            setText(it.languageName, false)
        }
        setAdapter(adapterLanguage)
        setOnItemClickListener { parent, view, position, id ->
            languageItem = langList[position].also {
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
            (activity as MainActivity).onRecreate()
        }?: kotlin.run {
            popFrom(R.id.fragmentLanguage)
        }
    }
}