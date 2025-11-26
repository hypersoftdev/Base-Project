package com.hypersoft.baseproject.presentation.mediaVideoDetails.ui

import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaVideoDetailBinding
import com.hypersoft.baseproject.presentation.mediaVideoDetails.effect.MediaVideoDetailEffect
import com.hypersoft.baseproject.presentation.mediaVideoDetails.intent.MediaVideoDetailIntent
import com.hypersoft.baseproject.presentation.mediaVideoDetails.state.MediaVideoDetailState
import com.hypersoft.baseproject.presentation.mediaVideoDetails.viewModel.MediaVideoDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

class MediaVideoDetailFragment : BaseFragment<FragmentMediaVideoDetailBinding>(FragmentMediaVideoDetailBinding::inflate) {

    private val navArgs: MediaVideoDetailFragmentArgs by navArgs()

    private val viewModel: MediaVideoDetailViewModel by viewModel()

    override fun onViewCreated() {
        setupVideoView()
        loadVideo()

        binding.mbBackMediaVideoDetail.setOnClickListener { viewModel.handleIntent(MediaVideoDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaVideoDetail.setOnClickListener { viewModel.handleIntent(MediaVideoDetailIntent.PlayPause) }
        binding.sliderMediaVideoDetail.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.handleIntent(MediaVideoDetailIntent.SeekTo(value.toInt()))
            }
        }
    }

    private fun setupVideoView() {
        binding.vvVideoMediaVideoDetail.setOnPreparedListener { mp ->
            // VideoView's MediaPlayer is ready
        }
        binding.vvVideoMediaVideoDetail.setOnCompletionListener {
            viewModel.handleIntent(MediaVideoDetailIntent.PlayPause)
        }
        binding.vvVideoMediaVideoDetail.setOnErrorListener { _, what, extra ->
            context?.showToast("Video playback error: $what")
            true
        }
    }

    private fun loadVideo() {
        viewModel.handleIntent(MediaVideoDetailIntent.LoadVideo(navArgs.videoUriPath))
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

    private fun renderState(state: MediaVideoDetailState) {
        // Update title
        binding.mtvTitleMediaVideoDetail.text = state.title

        // Update VideoView URI when video is loaded
        if (state.duration > 0 && binding.vvVideoMediaVideoDetail.tag != navArgs.videoUriPath) {
            binding.vvVideoMediaVideoDetail.setVideoURI(navArgs.videoUriPath.toUri())
            binding.vvVideoMediaVideoDetail.tag = navArgs.videoUriPath
        }

        // Update play/pause button icon
        binding.mbPlayPauseMediaVideoDetail.setIconResource(
            when (state.isPlaying) {
                true -> coreR.drawable.ic_svg_pause
                false -> coreR.drawable.ic_svg_play
            }
        )

        // Sync VideoView playback state with ViewModel state
        if (state.isPlaying) {
            if (!binding.vvVideoMediaVideoDetail.isPlaying) {
                binding.vvVideoMediaVideoDetail.start()
            }
        } else {
            if (binding.vvVideoMediaVideoDetail.isPlaying) {
                binding.vvVideoMediaVideoDetail.pause()
            }
        }

        // Sync VideoView seek position
        if (state.currentProgress > 0) {
            val currentPosition = binding.vvVideoMediaVideoDetail.currentPosition
            val diff = kotlin.math.abs(currentPosition - state.currentProgress)
            if (diff > 500) { // Only seek if difference is more than 500ms
                binding.vvVideoMediaVideoDetail.seekTo(state.currentProgress)
            }
        }

        // Update slider
        if (state.duration > 0) {
            binding.sliderMediaVideoDetail.valueTo = state.duration.toFloat()
            binding.sliderMediaVideoDetail.value = state.currentProgress.toFloat()
        }

        // Update time labels (MediaPlayer returns milliseconds)
        binding.mtvCurrentProgressMediaVideoDetail.text = state.currentProgress.toLong().toTimeFormat()
        binding.mtvTotalTimeMediaVideoDetail.text = state.duration.toLong().toTimeFormat()
    }

    private fun handleEffect(effect: MediaVideoDetailEffect) {
        when (effect) {
            is MediaVideoDetailEffect.NavigateBack -> popFrom(R.id.mediaVideoDetailFragment)
            is MediaVideoDetailEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}