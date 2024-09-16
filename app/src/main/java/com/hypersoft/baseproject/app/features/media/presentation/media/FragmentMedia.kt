package com.hypersoft.baseproject.app.features.media.presentation.media

import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentMediaBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.navigateTo
import com.hypersoft.baseproject.utilities.extensions.popFrom

class FragmentMedia : BaseFragment<FragmentMediaBinding>(FragmentMediaBinding::inflate) {

    override fun onViewCreated() {

        binding.mbBackMedia.setOnClickListener { popFrom(R.id.fragmentMedia) }
        binding.mbImageMedia.setOnClickListener { navigateTo(R.id.fragmentMedia, R.id.action_fragmentMedia_to_fragmentMediaImage) }
    }
}