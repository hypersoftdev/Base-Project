package com.hypersoft.baseproject.ui.fragments.country

import androidx.navigation.fragment.navArgs
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentCountryDetailBinding
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentCountryDetail : BaseFragment<FragmentCountryDetailBinding>(R.layout.fragment_country_detail) {

    private val args: FragmentCountryDetailArgs by navArgs()

    override fun onViewCreatedOneTime() {
        setUI()
    }

    override fun onViewCreatedEverytime() {

    }

    private fun setUI() {
        binding.tvResultsCountryDetail.text = args.countryTable.toString()
    }

    override fun navIconBackPressed() {
        popFrom(R.id.fragmentCountryDetail)
    }

    override fun onBackPressed() {
        popFrom(R.id.fragmentCountryDetail)
    }
}