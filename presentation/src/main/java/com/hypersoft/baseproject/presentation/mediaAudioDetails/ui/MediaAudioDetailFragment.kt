package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudioDetailBinding
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

class MediaAudioDetailFragment : BaseFragment<FragmentMediaAudioDetailBinding>(FragmentMediaAudioDetailBinding::inflate) {

    private val navArgs: MediaAudioDetailFragmentArgs by navArgs()

    private val viewModel: MediaAudioDetailViewModel by viewModel()

    override fun onViewCreated() {
        loadAudio()

        binding.mbBackMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.PlayPause) }
        binding.sliderMediaAudioDetail.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.handleIntent(MediaAudioDetailIntent.SeekTo(value.toInt()))
            }
        }
    }

    private fun loadAudio() {
        viewModel.handleIntent(MediaAudioDetailIntent.LoadAudio(navArgs.audioUriPath))
    }

    override fun initObservers() {
        observeState()
        observeEffect()
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun renderState(state: MediaAudioDetailState) {
        // Update title and artist
        binding.mtvTitleMediaAudioDetail.text = state.title
        binding.mtvArtistMediaAudioDetail.text = state.artist

        // Update play/pause button icon
        binding.mbPlayPauseMediaAudioDetail.setIconResource(
            when (state.isPlaying) {
                true -> coreR.drawable.ic_svg_pause
                false -> coreR.drawable.ic_svg_play
            }
        )

        // Update slider
        if (state.duration > 0) {
            binding.sliderMediaAudioDetail.valueTo = state.duration.toFloat()
            binding.sliderMediaAudioDetail.value = state.currentProgress.toFloat()
        }

        // Update time labels (MediaPlayer returns milliseconds)
        binding.mtvCurrentProgressMediaAudioDetail.text = state.currentProgress.toLong().toTimeFormat()
        binding.mtvTotalTimeMediaAudioDetail.text = state.duration.toLong().toTimeFormat()

        // Show/hide loading indicator if you have one
        binding.cpiLoadingMediaAudioDetail.isVisible = state.isLoading
    }

    private fun handleEffect(effect: MediaAudioDetailEffect) {
        when (effect) {
            is MediaAudioDetailEffect.NavigateBack -> popFrom(R.id.mediaAudioDetailFragment)
            is MediaAudioDetailEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}