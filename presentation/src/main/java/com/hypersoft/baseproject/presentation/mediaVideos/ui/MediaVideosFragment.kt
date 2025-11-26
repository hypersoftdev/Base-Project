package com.hypersoft.baseproject.presentation.mediaVideos.ui

import androidx.core.view.isVisible
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaVideosBinding
import com.hypersoft.baseproject.presentation.mediaVideos.adapter.MediaVideosAdapter
import com.hypersoft.baseproject.presentation.mediaVideos.effect.MediaVideosEffect
import com.hypersoft.baseproject.presentation.mediaVideos.intent.MediaVideosIntent
import com.hypersoft.baseproject.presentation.mediaVideos.state.MediaVideosState
import com.hypersoft.baseproject.presentation.mediaVideos.viewModel.MediaVideosViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaVideosFragment : BaseFragment<FragmentMediaVideosBinding>(FragmentMediaVideosBinding::inflate) {

    private val viewModel: MediaVideosViewModel by viewModel()

    private val adapter by lazy {
        MediaVideosAdapter { videoUri ->
            viewModel.handleIntent(MediaVideosIntent.VideoClicked(videoUri))
        }
    }

    override fun onViewCreated() {
        initRecyclerView()

        binding.mbBackMediaVideos.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun initRecyclerView() {
        binding.rcvListMediaVideos.adapter = adapter
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

    private fun renderState(state: MediaVideosState) {
        binding.cpiLoadingMediaVideos.isVisible = state.isLoading

        adapter.submitList(state.videos)

        state.error?.let {
            // Error is handled via effect
        }
    }

    private fun handleEffect(effect: MediaVideosEffect) {
        when (effect) {
            is MediaVideosEffect.NavigateToDetail -> navigateToDetail(effect.videoUri)
            is MediaVideosEffect.ShowError -> context?.showToast(effect.message)
        }
    }

    private fun navigateToDetail(videoUri: String) {
        val action = MediaVideosFragmentDirections.actionMediaVideosFragmentToMediaVideoDetailFragment(videoUri)
        navigateTo(R.id.mediaVideosFragment, action)
    }
}