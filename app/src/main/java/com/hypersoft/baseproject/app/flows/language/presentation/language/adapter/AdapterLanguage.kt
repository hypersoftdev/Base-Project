package com.hypersoft.baseproject.app.flows.language.presentation.language.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.databinding.ItemLanguageBinding
import com.hypersoft.baseproject.app.flows.language.domain.entities.ItemLanguage

class AdapterLanguage(private val clicker: (selectedCode: String) -> Unit) : ListAdapter<ItemLanguage, AdapterLanguage.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)

        bindViews(holder.binding, currentItem)

        holder.binding.mcvContainerItemLanguage.setOnClickListener { clicker.invoke(currentItem.languageCode) }
    }

    private fun bindViews(binding: ItemLanguageBinding, currentItem: ItemLanguage) {
        binding.apply {
            mtvLanguageItemLanguage.text = currentItem.languageName
            sivTickItemLanguage.isVisible = currentItem.isSelected
            sivOverlayItemLanguage.isVisible = currentItem.isSelected
            ifvImageItemLanguage.setImageResource(currentItem.flagIcon)
        }
    }

    inner class CustomViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)

    object DiffCallback : DiffUtil.ItemCallback<ItemLanguage>() {
        override fun areItemsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage): Boolean {
            return oldItem.languageCode == newItem.languageCode
        }

        override fun areContentsTheSame(oldItem: ItemLanguage, newItem: ItemLanguage): Boolean {
            return oldItem == newItem
        }
    }
}