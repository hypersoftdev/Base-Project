package com.hypersoft.baseproject.app.flows.language.presentation.inAppLanguage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.databinding.ItemInAppLanguageBinding
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage

class AdapterInAppLanguage(private val clicker: (selectedCode: String) -> Unit) : ListAdapter<ItemLanguage, AdapterInAppLanguage.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemInAppLanguageBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)

        bindViews(holder.binding, currentItem)

        holder.binding.mcvContainerItemInAppLanguage.setOnClickListener { clicker.invoke(currentItem.languageCode) }
    }

    private fun bindViews(binding: ItemInAppLanguageBinding, currentItem: ItemLanguage) {
        binding.apply {
            mtvLanguageItemInAppLanguage.text = currentItem.languageName
            sivTickItemInAppLanguage.isVisible = currentItem.isSelected
            sivOverlayItemInAppLanguage.isVisible = currentItem.isSelected
            ifvImageItemInAppLanguage.setImageResource(currentItem.flagIcon)
        }
    }

    inner class CustomViewHolder(val binding: ItemInAppLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    object DiffCallback : DiffUtil.ItemCallback<ItemLanguage>() {
        override fun areItemsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage): Boolean {
            return oldItem.languageCode == newItem.languageCode
        }

        override fun areContentsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage): Boolean {
            return oldItem == newItem
        }
    }
}