package com.hypersoft.baseproject.app.features.media.presentation.images.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.hypersoft.baseproject.app.features.media.data.images.dataSources.MediaStoreMediaImages
import com.hypersoft.baseproject.app.features.media.data.images.repository.RepositoryMediaImages
import com.hypersoft.baseproject.app.features.media.domain.images.useCases.UseCaseMediaImageDetail
import com.hypersoft.baseproject.app.features.media.presentation.images.adapter.recyclerView.AdapterMediaImageDetail
import com.hypersoft.baseproject.app.features.media.presentation.images.viewModels.ViewModelMediaImageDetail
import com.hypersoft.baseproject.app.features.media.presentation.images.viewModels.ViewModelMediaImageDetailProvider
import com.hypersoft.baseproject.databinding.FragmentMediaImageDetailBinding
import com.hypersoft.baseproject.utilities.base.fragment.BaseFragment
import com.hypersoft.baseproject.utilities.extensions.showToast
import com.hypersoft.baseproject.utilities.utils.ConstantUtils

class FragmentMediaImageDetail : BaseFragment<FragmentMediaImageDetailBinding>(FragmentMediaImageDetailBinding::inflate) {

    private val adapterEnhanceGalleryDetail by lazy { AdapterMediaImageDetail(itemClick) }
    private val argFolderName by lazy { arguments?.getString(ARG_FOLDER_NAME, ConstantUtils.GALLERY_UNKNOWN) ?: ConstantUtils.GALLERY_UNKNOWN }

    // MVVM
    private val mediaStoreMediaImages by lazy { MediaStoreMediaImages(context?.contentResolver) }
    private val repositoryMediaImages by lazy { RepositoryMediaImages(mediaStoreMediaImages) }
    private val useCaseMediaImageDetail by lazy { UseCaseMediaImageDetail(repositoryMediaImages) }
    private val viewModelMediaImageDetail by viewModels<ViewModelMediaImageDetail> { ViewModelMediaImageDetailProvider(useCaseMediaImageDetail) }

    override fun onViewCreated() {
        initRecyclerView()
        initObservers()
        fetchData()

        binding.srlRefreshMediaImageDetail.setOnRefreshListener { fetchData() }
    }

    private fun initRecyclerView() {
        binding.rcvListMediaImageDetail.adapter = adapterEnhanceGalleryDetail
    }

    private fun initObservers() {
        viewModelMediaImageDetail.imagesLiveData.observe(viewLifecycleOwner) {
            binding.progressBarMediaImageDetail.visibility = View.GONE
            binding.srlRefreshMediaImageDetail.isRefreshing = false
            adapterEnhanceGalleryDetail.submitList(it)
        }
        viewModelMediaImageDetail.navigateScreenLiveData.observe(viewLifecycleOwner) {
            navigateDetailScreen(it)
        }
        viewModelMediaImageDetail.refreshLiveData.observe(viewLifecycleOwner) {
            fetchData()
        }
        viewModelMediaImageDetail.errorLiveData.observe(viewLifecycleOwner) {
            binding.progressBarMediaImageDetail.visibility = View.GONE
            context.showToast(it)
        }
    }

    private fun fetchData() {
        viewModelMediaImageDetail.getImages(argFolderName)
    }

    private val itemClick: ((Uri) -> Unit) = { viewModelMediaImageDetail.imageClick(it) }

    private fun navigateDetailScreen(imageUriPath: String) {
        val action = FragmentMediaImageDirections.actionFragmentMediaImageToFragmentMediaImageDisplay(imageUriPath)
        diComponent.generalObserver._navigationDirectionsMediaImageLiveData.value = action
    }

    companion object {
        private const val ARG_FOLDER_NAME = "folderName"

        fun newInstance(folderName: String): FragmentMediaImageDetail {
            val fragment = FragmentMediaImageDetail()
            val bundle = Bundle().apply {
                putString(ARG_FOLDER_NAME, folderName)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}