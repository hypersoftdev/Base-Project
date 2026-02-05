package com.hypersoft.baseproject.presentation.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.entities.Language
import com.hypersoft.baseproject.presentation.databinding.ItemLanguageBinding

class LanguageAdapter : ListAdapter<Language, LanguageAdapter.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindViews(getItem(position))
    }

    class CustomViewHolder(private val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindViews(currentItem: Language) {
            binding.apply {
                // Fill views
                ifvImageItemLanguage.setImageResource(currentItem.flagIcon)
                mtvLanguageItemLanguage.text = currentItem.languageName

                // Selection
                sivTickItemLanguage.isVisible = currentItem.isSelected
                sivOverlayItemLanguage.isVisible = currentItem.isSelected

                // Clicks
                mcvContainerItemLanguage.setOnClickListener { currentItem.itemClick() }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Language>() {
        override fun areItemsTheSame(oldItem: Language, newItem: Language) = oldItem.languageCode == newItem.languageCode
        override fun areContentsTheSame(oldItem: Language, newItem: Language): Boolean = oldItem == newItem
    }
}