package com.hypersoft.baseproject.app.flows.premium

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.utilities.base.BaseFragment
import com.hypersoft.baseproject.databinding.FragmentPremiumBinding
import com.hypersoft.baseproject.utilities.extensions.popFrom

class FragmentPremium : BaseFragment<FragmentPremiumBinding>(FragmentPremiumBinding::inflate) {

    override fun onViewCreated() {
        binding.mbBackHome.setOnClickListener { popFrom(R.id.fragmentPremium) }
    }

}