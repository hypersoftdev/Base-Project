package com.hypersoft.baseproject.presentation.media.ui

import com.hypersoft.baseproject.core.extensions.collectWhenCreated
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.permission.PermissionManager
import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.core.permission.result.PermissionResult
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.base.fragment.BaseFragment
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaBinding
import com.hypersoft.baseproject.presentation.media.effect.MediaEffect
import com.hypersoft.baseproject.presentation.media.intent.MediaIntent
import com.hypersoft.baseproject.presentation.media.state.MediaState
import com.hypersoft.baseproject.presentation.media.viewModel.MediaViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

class MediaFragment : BaseFragment<FragmentMediaBinding>(FragmentMediaBinding::inflate) {

    private val viewModel: MediaViewModel by viewModel()
    private val permissionManager = PermissionManager(this)

    override fun onViewCreated() {

        binding.mbBackMedia.setOnClickListener { viewModel.handleIntent(MediaIntent.NavigateBack) }
        binding.mbAudioMedia.setOnClickListener { requestPermission(MediaPermission.AUDIOS, MediaIntent.NavigateToAudios) }
        binding.mbImageMedia.setOnClickListener { requestPermission(MediaPermission.IMAGES_VIDEOS, MediaIntent.NavigateToImages) }
        binding.mbVideoMedia.setOnClickListener { requestPermission(MediaPermission.IMAGES_VIDEOS, MediaIntent.NavigateToVideos) }
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { renderState(it) }
        collectWhenCreated(viewModel.effect) { handleEffect(it) }
    }

    private fun renderState(state: MediaState) {
        // Handle state updates if needed
    }

    private fun handleEffect(effect: MediaEffect) {
        when (effect) {
            is MediaEffect.NavigateBack -> popFrom(R.id.mediaFragment)
            is MediaEffect.NavigateToImages -> navigateTo(R.id.mediaFragment, R.id.action_mediaFragment_to_mediaImagesFragment)
            is MediaEffect.NavigateToVideos -> navigateTo(R.id.mediaFragment, R.id.action_mediaFragment_to_mediaVideosFragment)
            is MediaEffect.NavigateToAudios -> navigateTo(R.id.mediaFragment, R.id.action_mediaFragment_to_mediaAudiosFragment)
            is MediaEffect.ShowError -> context.showToast(effect.message)
        }
    }

    private fun requestPermission(mediaPermission: MediaPermission, mediaIntent: MediaIntent) {
        permissionManager.requestPermission(mediaPermission) { result ->
            when (result) {
                PermissionResult.GrantedFull -> viewModel.handleIntent(mediaIntent)
                PermissionResult.GrantedLimited -> viewModel.handleIntent(mediaIntent)
                PermissionResult.Denied -> context.showToast(coreR.string.permission_denied)
            }
        }
    }
}