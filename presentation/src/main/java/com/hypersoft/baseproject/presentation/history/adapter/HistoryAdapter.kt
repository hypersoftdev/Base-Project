package com.hypersoft.baseproject.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.entities.History
import com.hypersoft.baseproject.presentation.databinding.ItemHistoryBinding

class HistoryAdapter : ListAdapter<History, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHistoryBinding.inflate(layoutInflater, parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: History) {
            binding.root.text = history.title
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean = oldItem.title == newItem.title
        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean = oldItem == newItem
    }
}