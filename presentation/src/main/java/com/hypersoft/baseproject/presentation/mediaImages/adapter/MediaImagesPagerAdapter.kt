package com.hypersoft.baseproject.presentation.mediaImages.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hypersoft.baseproject.core.constants.Constants
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.presentation.mediaImagesTab.ui.ImagesTabFragment

class MediaImagesPagerAdapter(fragment: Fragment, folders: List<ImageFolderEntity>) : FragmentStateAdapter(fragment) {

    private val allFolder = ImageFolderEntity(Constants.GALLERY_ALL, folders.sumOf { it.imageCount })

    val allFolders = listOf(allFolder) + folders

    val titles: List<String> get() = allFolders.map { it.folderName }

    override fun getItemCount(): Int = allFolders.size

    override fun createFragment(position: Int): Fragment {
        val folderName = allFolders[position].folderName
        return ImagesTabFragment.Companion.newInstance(folderName)
    }
}