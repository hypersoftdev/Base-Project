package com.hypersoft.baseproject.app.features.history.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.databinding.ItemHistoryBinding
import com.hypersoft.baseproject.app.features.history.domain.entities.ItemHistory

class AdapterHistory : ListAdapter<ItemHistory, AdapterHistory.CustomViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(layoutInflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = getItem(position)
        bindViews(holder.binding, currentItem)
    }

    private fun bindViews(binding: ItemHistoryBinding, currentItem: ItemHistory) {
        binding.root.text = currentItem.title
    }

    inner class CustomViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    object DiffCallback : DiffUtil.ItemCallback<ItemHistory>() {
        override fun areItemsTheSame(oldItem: ItemHistory, newItem: ItemHistory): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ItemHistory, newItem: ItemHistory): Boolean {
            return oldItem == newItem
        }
    }
}