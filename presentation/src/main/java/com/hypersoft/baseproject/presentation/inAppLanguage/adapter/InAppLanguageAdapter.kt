package com.hypersoft.baseproject.presentation.inAppLanguage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language
import com.hypersoft.baseproject.presentation.databinding.ItemInAppLanguageBinding

class InAppLanguageAdapter : ListAdapter<Language, InAppLanguageAdapter.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemInAppLanguageBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindViews(getItem(position))
    }

    class CustomViewHolder(private val binding: ItemInAppLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: Language) {
            binding.apply {
                // Fill views
                ifvImageItemInAppLanguage.setImageResource(currentItem.flagIcon)
                mtvLanguageItemInAppLanguage.text = currentItem.languageName

                // Selection
                sivTickItemInAppLanguage.isVisible = currentItem.isSelected
                sivOverlayItemInAppLanguage.isVisible = currentItem.isSelected

                // Clicks
                mcvContainerItemInAppLanguage.setOnClickListener { currentItem.itemClick() }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Language>() {
        override fun areItemsTheSame(oldItem: Language, newItem: Language) = oldItem.languageCode == newItem.languageCode
        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean = oldItem == newItem
    }
}