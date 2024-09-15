package com.hypersoft.baseproject.app.features.media.presentation.images.adapter.viewPager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImageFolder
import com.hypersoft.baseproject.app.features.media.presentation.images.ui.FragmentMediaImageDetail
import com.hypersoft.baseproject.utilities.utils.ConstantUtils

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class PagerAdapterMediaImage(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var folderList = arrayListOf<ItemMediaImageFolder>()

    override fun getItemCount(): Int = folderList.size

    override fun createFragment(position: Int): Fragment {
        val folderName = when (position == 0) {
            true -> ConstantUtils.GALLERY_ALL
            false -> folderList[position].folderName
        }
        return FragmentMediaImageDetail.newInstance(folderName)
    }

    fun addFolder(folder: ItemMediaImageFolder) {
        folderList.add(folder)
    }

    fun getFolderName(position: Int): String {
        return folderList[position].folderName
    }
}