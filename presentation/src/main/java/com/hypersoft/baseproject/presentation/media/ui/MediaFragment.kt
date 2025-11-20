package com.hypersoft.baseproject.presentation.media.ui

import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaBinding
import com.hypersoft.baseproject.presentation.media.effect.MediaEffect
import com.hypersoft.baseproject.presentation.media.intent.MediaIntent
import com.hypersoft.baseproject.presentation.media.state.MediaState
import com.hypersoft.baseproject.presentation.media.viewModel.MediaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : BaseFragment<FragmentMediaBinding>(FragmentMediaBinding::inflate) {

    private val viewModel: MediaViewModel by viewModel()

    override fun onViewCreated() {
        binding.mbBackMedia.setOnClickListener { viewModel.handleIntent(MediaIntent.NavigateBack) }
        binding.mbImageMedia.setOnClickListener { viewModel.handleIntent(MediaIntent.NavigateToImages) }
        binding.mbVideoMedia.setOnClickListener { viewModel.handleIntent(MediaIntent.NavigateToVideos) }
        binding.mbAudioMedia.setOnClickListener { viewModel.handleIntent(MediaIntent.NavigateToAudios) }
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

    private fun renderState(state: MediaState) {
        // Handle state updates if needed
    }

    private fun handleEffect(effect: MediaEffect) {
        when (effect) {
            is MediaEffect.NavigateBack -> popFrom(R.id.fragmentMedia)
            is MediaEffect.NavigateToImages -> navigateTo(R.id.fragmentMedia, R.id.action_fragmentMedia_to_fragmentMediaImage)
            is MediaEffect.NavigateToVideos -> navigateTo(R.id.fragmentMedia, R.id.action_fragmentMedia_to_fragmentMediaVideo)
            is MediaEffect.NavigateToAudios -> navigateTo(R.id.fragmentMedia, R.id.action_fragmentMedia_to_fragmentMediaAudio)
            is MediaEffect.ShowError -> context.showToast(effect.message)
        }
    }
}