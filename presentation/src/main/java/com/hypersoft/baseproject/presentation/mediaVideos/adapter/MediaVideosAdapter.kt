package com.hypersoft.baseproject.presentation.mediaVideos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.core.R
import com.hypersoft.baseproject.core.extensions.loadImage
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import com.hypersoft.baseproject.presentation.databinding.ItemMediaVideoBinding

class MediaVideosAdapter(private val onVideoClick: (String) -> Unit) : ListAdapter<VideoEntity, MediaVideosAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemMediaVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding, onVideoClick)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VideoViewHolder(private val binding: ItemMediaVideoBinding, private val onVideoClick: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideoEntity) {
            binding.ifvThumbnailItemMediaVideo.loadImage(video.uri, placeholder = R.drawable.ic_svg_video, error = R.drawable.ic_svg_video)
            binding.mtvTitleItemMediaVideo.text = video.displayName
            binding.mtvDurationItemMediaVideo.text = video.duration.toTimeFormat()

            binding.root.setOnClickListener { onVideoClick(video.uri.toString()) }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoEntity>() {
        override fun areItemsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VideoEntity, newItem: VideoEntity): Boolean = oldItem == newItem
    }
}