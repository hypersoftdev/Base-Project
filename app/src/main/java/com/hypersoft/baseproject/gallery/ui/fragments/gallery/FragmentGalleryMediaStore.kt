package com.hypersoft.baseproject.gallery.ui.fragments.gallery

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.hypersoft.baseproject.R
import com.hypersoft.baseproject.databinding.FragmentGalleryMediaStoreBinding
import com.hypersoft.baseproject.gallery.helper.adapters.interfaces.OnPictureItemClickListener
import com.hypersoft.baseproject.gallery.helper.adapters.recyclerView.AdapterGallery
import com.hypersoft.baseproject.gallery.helper.models.Picture
import com.hypersoft.baseproject.ui.fragments.base.BaseFragment

class FragmentGalleryMediaStore :
    BaseFragment<FragmentGalleryMediaStoreBinding>(R.layout.fragment_gallery_media_store) {

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var adapterGallery: AdapterGallery

    override fun onViewCreatedOneTime() {
        initRecyclerView()
        initObservers()

        binding.fabDoneGallery.setOnClickListener { onDoneClick() }
    }

    override fun onViewCreatedEverytime() {

    }

    private fun initRecyclerView() {
        adapterGallery = AdapterGallery(object : OnPictureItemClickListener {
            override fun onPictureClick(picture: Picture) {
                viewModel.onPictureClick(picture)
            }
        })
        binding.recyclerViewImagesGallery.adapter = adapterGallery
    }

    private fun initObservers() = with(viewModel) {
        fetchFiles()
        picturesLiveData.observe(viewLifecycleOwner) {
            binding.progressBarGallery.visibility = View.GONE
            adapterGallery.submitList(it)
        }
    }

    private fun onDoneClick() {
        // Use the Kotlin extension in the fragment-ktx artifact
        val picturesArray = viewModel.selectedPicturesList.toTypedArray()
        setFragmentResult("requestKey", bundleOf("bundleKey" to picturesArray))
        onBackPressed()
    }

    override fun navIconBackPressed() {
        popFrom(R.id.fragmentGallery)
    }

    override fun onBackPressed() {
        popFrom(R.id.fragmentGallery)
    }
}