package com.hypersoft.baseproject.presentation.mediaAudios.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hypersoft.baseproject.core.R
import com.hypersoft.baseproject.core.extensions.loadImage
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.presentation.databinding.ItemMediaAudioBinding

class MediaAudiosAdapter(private val onAudioClick: (String) -> Unit) : ListAdapter<AudioEntity, MediaAudiosAdapter.AudioViewHolder>(AudioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val binding = ItemMediaAudioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AudioViewHolder(binding, onAudioClick)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AudioViewHolder(private val binding: ItemMediaAudioBinding, private val onAudioClick: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(audio: AudioEntity) {
            binding.ifvThumbnailItemMediaAudio.loadImage(R.drawable.ic_svg_audio)
            binding.mtvTitleItemMediaAudio.text = audio.displayName
            binding.mtvArtistItemMediaAudio.text = audio.artist.ifEmpty { "Unknown Artist" }
            binding.mtvDurationItemMediaAudio.text = audio.duration.toTimeFormat()

            binding.root.setOnClickListener { onAudioClick(audio.uri.toString()) }
        }
    }

    class AudioDiffCallback : DiffUtil.ItemCallback<AudioEntity>() {
        override fun areItemsTheSame(oldItem: AudioEntity, newItem: AudioEntity): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AudioEntity, newItem: AudioEntity): Boolean = oldItem == newItem
    }
}