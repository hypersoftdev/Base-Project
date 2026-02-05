package com.hypersoft.baseproject.presentation.mediaImagesTab.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.hypersoft.baseproject.core.extensions.collectWhenCreated
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.presentation.base.fragment.BaseFragment
import com.hypersoft.baseproject.presentation.databinding.FragmentImagesTabBinding
import com.hypersoft.baseproject.presentation.mediaImagesTab.adapter.MediaImagesAdapter
import com.hypersoft.baseproject.presentation.mediaImagesTab.effect.ImagesTabEffect
import com.hypersoft.baseproject.presentation.mediaImagesTab.intent.ImagesTabIntent
import com.hypersoft.baseproject.presentation.mediaImagesTab.state.ImagesTabState
import com.hypersoft.baseproject.presentation.mediaImagesTab.viewModel.ImagesTabViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ImagesTabFragment : BaseFragment<FragmentImagesTabBinding>(FragmentImagesTabBinding::inflate) {

    private val folderName: String by lazy { arguments?.getString(ARG_FOLDER_NAME) ?: "" }
    private val viewModel: ImagesTabViewModel by viewModel { parametersOf(folderName) }

    private val adapter by lazy {
        MediaImagesAdapter { imageUri ->
            viewModel.handleIntent(ImagesTabIntent.ImageClicked(imageUri))
        }
    }

    override fun onViewCreated() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rcvListImagesTab.adapter = adapter
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { renderState(it) }
        collectWhenCreated(viewModel.effect) { handleEffect(it) }
    }

    private fun renderState(state: ImagesTabState) {
        binding.cpiLoadingImagesTab.isVisible = state.isLoading
        adapter.submitList(state.images)
    }

    private fun handleEffect(effect: ImagesTabEffect) {
        when (effect) {
            is ImagesTabEffect.NavigateToDetail -> (parentFragment as? OnImageClickListener)?.onImageClick(effect.imageUri)
            is ImagesTabEffect.ShowError -> context?.showToast(effect.message)
        }
    }

    interface OnImageClickListener {
        fun onImageClick(imageUri: String)
    }

    companion object {
        private const val ARG_FOLDER_NAME = "folder_name"

        fun newInstance(folderName: String): ImagesTabFragment {
            return ImagesTabFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FOLDER_NAME, folderName)
                }
            }
        }
    }
}