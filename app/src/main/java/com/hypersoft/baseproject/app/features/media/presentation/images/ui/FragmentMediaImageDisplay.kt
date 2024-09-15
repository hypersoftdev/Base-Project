package com.hypersoft.baseproject.app.features.media.presentation.images.ui

import androidx.navigation.fragment.navArgs
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentMediaImageDisplayBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.popFrom
import com.hypersoft.baseproject.utilities.extensions.setImageFromUriPath

class FragmentMediaImageDisplay : BaseFragment<FragmentMediaImageDisplayBinding>(FragmentMediaImageDisplayBinding::inflate) {

    private val navArgs by navArgs<FragmentMediaImageDisplayArgs>()


    override fun onViewCreated() {
        setUI()

        binding.mbBackMediaImageDisplay.setOnClickListener { popFrom(R.id.fragmentMediaImageDisplay) }
    }

    private fun setUI() {
        binding.sivImageMediaImageDisplay.setImageFromUriPath(navArgs.imageUriPath)
    }
}