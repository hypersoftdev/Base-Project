package com.hypersoft.baseproject.ui.fragments.country

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentCountryBinding
import com.hypersoft.baseproject.helpers.adapters.recyclerView.AdapterCountry
import com.hypersoft.baseproject.helpers.dataProvider.DPCountry
import com.hypersoft.baseproject.helpers.interfaces.OnCountryItemClickListener
import com.hypersoft.baseproject.roomdb.tables.CountryTable
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentCountry : BaseFragment<FragmentCountryBinding>(R.layout.fragment_country) {

    private lateinit var adapterCountry: AdapterCountry
    private val dpCountry by lazy { DPCountry() }

    override fun onViewCreatedOneTime() {
        initRecyclerView()
        fillList()
    }

    override fun onViewCreatedEverytime() {

    }

    private fun initRecyclerView() {
        adapterCountry = AdapterCountry(object : OnCountryItemClickListener {
            override fun onCountryClick(countryTable: CountryTable) {
                diComponent.generalProjectViewModel.insertCountry(countryTable)
                onBackPressed()
            }
        })
        binding.rvOriginalListCountry.adapter = adapterCountry
    }

    private fun fillList() = adapterCountry.submitList(dpCountry.countryTableList)

    override fun navIconBackPressed() {
        popFrom(R.id.fragmentCountry)
    }

    override fun onBackPressed() {
        popFrom(R.id.fragmentCountry)
    }

}