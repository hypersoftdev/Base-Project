package com.hypersoft.baseproject.presentation.mediaAudios.ui

import androidx.core.view.isVisible
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudiosBinding
import com.hypersoft.baseproject.presentation.mediaAudios.adapter.MediaAudiosAdapter
import com.hypersoft.baseproject.presentation.mediaAudios.effect.MediaAudiosEffect
import com.hypersoft.baseproject.presentation.mediaAudios.intent.MediaAudiosIntent
import com.hypersoft.baseproject.presentation.mediaAudios.state.MediaAudiosState
import com.hypersoft.baseproject.presentation.mediaAudios.viewModel.MediaAudiosViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaAudiosFragment : BaseFragment<FragmentMediaAudiosBinding>(FragmentMediaAudiosBinding::inflate) {

    private val viewModel: MediaAudiosViewModel by viewModel()

    private val adapter by lazy {
        MediaAudiosAdapter { audioUri ->
            viewModel.handleIntent(MediaAudiosIntent.AudioClicked(audioUri))
        }
    }

    override fun onViewCreated() {
        initRecyclerView()

        binding.mbBackMediaAudios.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun initRecyclerView() {
        binding.rcvListMediaAudios.adapter = adapter
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

    private fun renderState(state: MediaAudiosState) {
        binding.cpiLoadingMediaAudios.isVisible = state.isLoading

        adapter.submitList(state.audios)

        state.error?.let {
            // Error is handled via effect
        }
    }

    private fun handleEffect(effect: MediaAudiosEffect) {
        when (effect) {
            is MediaAudiosEffect.NavigateToDetail -> navigateToDetail(effect.audioUri)
            is MediaAudiosEffect.ShowError -> context?.showToast(effect.message)
        }
    }

    private fun navigateToDetail(audioUri: String) {
        val action = MediaAudiosFragmentDirections.actionMediaAudiosFragmentToMediaAudioDetailFragment(audioUri)
        navigateTo(R.id.mediaAudiosFragment, action)
    }
}