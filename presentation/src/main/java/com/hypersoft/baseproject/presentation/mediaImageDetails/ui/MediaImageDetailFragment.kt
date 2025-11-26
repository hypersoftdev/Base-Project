package com.hypersoft.baseproject.presentation.mediaImageDetails.ui

import androidx.navigation.fragment.navArgs
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.loadImage
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaImageDetailBinding
import com.hypersoft.baseproject.presentation.mediaImageDetails.effect.MediaImageDetailEffect
import com.hypersoft.baseproject.presentation.mediaImageDetails.intent.MediaImageDetailIntent
import com.hypersoft.baseproject.presentation.mediaImageDetails.state.MediaImageDetailState
import com.hypersoft.baseproject.presentation.mediaImageDetails.viewModel.MediaImageDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaImageDetailFragment : BaseFragment<FragmentMediaImageDetailBinding>(FragmentMediaImageDetailBinding::inflate) {

    private val navArgs: MediaImageDetailFragmentArgs by navArgs()

    private val viewModel: MediaImageDetailViewModel by viewModel { parametersOf(navArgs.imageUriPath) }

    override fun onViewCreated() {
        binding.mbBackMediaImageDetail.setOnClickListener { viewModel.handleIntent(MediaImageDetailIntent.NavigateBack) }
    }

    override fun initObservers() {
        observeState()
        observeEffects()
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun observeEffects() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: MediaImageDetailState) {
        binding.ifvImageMediaImageDetail.loadImage(state.imageUri)
    }

    private fun handleEffect(effect: MediaImageDetailEffect) {
        when (effect) {
            is MediaImageDetailEffect.NavigateBack -> popFrom(R.id.mediaImageDetailFragment)
        }
    }
}