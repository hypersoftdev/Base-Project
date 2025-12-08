package com.hypersoft.baseproject.presentation.mediaImages.ui

import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.permission.PermissionManager
import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaImagesBinding
import com.hypersoft.baseproject.presentation.mediaImages.adapter.MediaImagesPagerAdapter
import com.hypersoft.baseproject.presentation.mediaImages.effect.MediaImagesEffect
import com.hypersoft.baseproject.presentation.mediaImages.enums.MediaImagesPermissionLevel
import com.hypersoft.baseproject.presentation.mediaImages.intent.MediaImagesIntent
import com.hypersoft.baseproject.presentation.mediaImages.state.MediaImagesState
import com.hypersoft.baseproject.presentation.mediaImages.viewModel.MediaImagesViewModel
import com.hypersoft.baseproject.presentation.mediaImagesTab.ui.ImagesTabFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaImagesFragment : BaseFragment<FragmentMediaImagesBinding>(FragmentMediaImagesBinding::inflate), ImagesTabFragment.OnImageClickListener {

    private val viewModel: MediaImagesViewModel by viewModel()
    private val permissionManager = PermissionManager(this)

    private var pagerAdapter: MediaImagesPagerAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated() {
        binding.toolbarMediaImages.setNavigationOnClickListener { viewModel.handleIntent(MediaImagesIntent.NavigationBack) }
        binding.mbGrantPermissionMediaImages.setOnClickListener { viewModel.handleIntent(MediaImagesIntent.GrantPermissionClick) }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionState()
    }

    private fun updatePermissionState() {
        val level = when {
            permissionManager.isPermissionGranted(MediaPermission.IMAGES_VIDEOS) -> MediaImagesPermissionLevel.Full
            permissionManager.isLimitedPermissionGranted(MediaPermission.IMAGES_VIDEOS) -> MediaImagesPermissionLevel.Limited
            else -> MediaImagesPermissionLevel.Denied
        }

        viewModel.handleIntent(MediaImagesIntent.PermissionChanged(level))
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

    private fun renderState(state: MediaImagesState) {
        when (state.permission) {
            MediaImagesPermissionLevel.Idle -> {}
            MediaImagesPermissionLevel.Full -> binding.llLimitedPermissionWarningMediaImages.isVisible = false
            MediaImagesPermissionLevel.Limited -> binding.llLimitedPermissionWarningMediaImages.isVisible = true
            MediaImagesPermissionLevel.Denied -> {
                viewModel.handleIntent(MediaImagesIntent.NavigationBack)
                return
            }
        }

        initViewPager(state.folders)
    }

    private fun initViewPager(folders: List<ImageFolderEntity>) {
        pagerAdapter = MediaImagesPagerAdapter(this, folders).also { adapter ->
            binding.vpContainerMediaImages.adapter = adapter

            tabLayoutMediator = TabLayoutMediator(binding.tlContainerMediaImages, binding.vpContainerMediaImages) { tab, position ->
                tab.text = adapter.titles[position]
            }.also {
                it.attach()
            }
        }
    }

    private fun handleEffect(effect: MediaImagesEffect) {
        when (effect) {
            is MediaImagesEffect.NavigateBack -> popFrom(R.id.mediaImagesFragment)
            is MediaImagesEffect.GrantPermissionClick -> permissionManager.openSettingsForPermission(type = MediaPermission.IMAGES_VIDEOS) {}
            is MediaImagesEffect.NavigateToDetail -> navigateToDetail(effect.imageUri)
            is MediaImagesEffect.ShowError -> context?.showToast(effect.message)
        }
    }

    override fun onImageClick(imageUri: String) {
        viewModel.handleIntent(MediaImagesIntent.ImageClicked(imageUri))
    }

    private fun navigateToDetail(imageUri: String) {
        val action = MediaImagesFragmentDirections.actionMediaImagesFragmentToMediaImageDetailFragment(imageUri)
        navigateTo(R.id.mediaImagesFragment, action)
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        pagerAdapter = null
        super.onDestroyView()
    }
}