package com.hypersoft.baseproject.presentation.mediaImages.ui

import com.google.android.material.tabs.TabLayoutMediator
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.navigateTo
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showSnackBar
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.permission.PermissionManager
import com.hypersoft.baseproject.core.permission.enums.MediaPermission
import com.hypersoft.baseproject.core.permission.result.PermissionResult
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaImagesBinding
import com.hypersoft.baseproject.presentation.mediaImages.adapter.MediaImagesPagerAdapter
import com.hypersoft.baseproject.presentation.mediaImages.effect.MediaImagesEffect
import com.hypersoft.baseproject.presentation.mediaImages.intent.MediaImagesIntent
import com.hypersoft.baseproject.presentation.mediaImages.state.MediaImagesState
import com.hypersoft.baseproject.presentation.mediaImages.viewModel.MediaImagesViewModel
import com.hypersoft.baseproject.presentation.mediaImagesTab.ui.ImagesTabFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

class MediaImagesFragment : BaseFragment<FragmentMediaImagesBinding>(FragmentMediaImagesBinding::inflate), ImagesTabFragment.OnImageClickListener {

    private val viewModel: MediaImagesViewModel by viewModel()
    private val permissionManager = PermissionManager(this)

    private var pagerAdapter: MediaImagesPagerAdapter? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    // 0 = denied, 1 = limited, 2 = full
    private var lastPermissionState: Int = -1

    override fun onViewCreated() {
        checkForPermission()

        binding.toolbarMediaImages.setNavigationOnClickListener { popFrom(R.id.mediaImagesFragment) }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionChange()
    }

    private fun checkForPermission() {
        permissionManager.checkPermissionGranted(MediaPermission.IMAGES_VIDEOS) { result ->
            lastPermissionState = when (result) {
                PermissionResult.GrantedFull -> 2
                PermissionResult.GrantedLimited -> 1
                PermissionResult.Denied -> 0
            }
        }
        if (permissionManager.isLimitedPermissionGranted(MediaPermission.IMAGES_VIDEOS)) {
            context.showSnackBar(messageResId = coreR.string.limited_access_warning_message, actionResId = coreR.string.grant) {
                permissionManager.openSettingsForPermission(MediaPermission.IMAGES_VIDEOS) {
                    // return from system settings → onResume() will handle refresh
                }
            }
        }
    }

    private fun checkPermissionChange() {
        val current = currentPermissionState()
        if (current != lastPermissionState) {
            lastPermissionState = current
            when (current) {
                1, 2 -> viewModel.handleIntent(MediaImagesIntent.RefreshFolders)
                else -> checkForPermission()
            }
        }
    }

    private fun currentPermissionState(): Int {
        return when {
            permissionManager.isPermissionGranted(MediaPermission.IMAGES_VIDEOS) -> 2
            permissionManager.isLimitedPermissionGranted(MediaPermission.IMAGES_VIDEOS) -> 1
            else -> 0
        }
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
        if (state.folders.isNotEmpty() && pagerAdapter == null) {
            initViewPager(state.folders)
        }
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