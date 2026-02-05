package com.hypersoft.baseproject.presentation.mediaImagesTab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.core.extensions.loadImage
import com.hypersoft.baseproject.domain.media.entities.ImageEntity
import com.hypersoft.baseproject.presentation.databinding.ItemMediaImageBinding

class MediaImagesAdapter(private val onImageClick: (String) -> Unit) : ListAdapter<ImageEntity, MediaImagesAdapter.ImageViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemMediaImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, onImageClick)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageViewHolder(private val binding: ItemMediaImageBinding, private val onImageClick: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: ImageEntity) {
            binding.ifvThumbnailItemMediaImage.loadImage(image.uri)
            binding.root.setOnClickListener { onImageClick(image.uri.toString()) }
        }
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageEntity>() {
        override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean = oldItem == newItem
    }
}