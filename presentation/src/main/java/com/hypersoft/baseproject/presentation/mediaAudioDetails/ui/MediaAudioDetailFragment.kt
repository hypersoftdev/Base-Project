package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import android.media.MediaPlayer
import android.net.Uri
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudioDetailBinding
import java.io.IOException

class MediaAudioDetailFragment : BaseFragment<FragmentMediaAudioDetailBinding>(FragmentMediaAudioDetailBinding::inflate) {

    private var mediaPlayer: MediaPlayer? = null
    private var audioUri: String? = null
    private var isPlaying = false

    override fun onViewCreated() {
        audioUri = arguments?.getString("audioUriPath")


        binding.mbBackMediaAudioDetail.setOnClickListener { popFrom(R.id.mediaAudiosFragment) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { togglePlayPause() }

        audioUri?.let { uri ->
            loadAudioInfo(uri)
            initializeMediaPlayer(uri)
        }
    }

    private fun loadAudioInfo(uriString: String) {
        // For now, just show the URI. You can enhance this to load metadata
        binding.mtvTitleMediaAudioDetail.text = "Audio File"
        binding.mtvArtistMediaAudioDetail.text = uriString
    }

    private fun initializeMediaPlayer(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(requireContext(), uri)
                prepareAsync()
                setOnPreparedListener {
                    // MediaPlayer is ready
                }
                setOnCompletionListener {
                    this@MediaAudioDetailFragment.isPlaying = false
                    updatePlayPauseButton()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun togglePlayPause() {
        mediaPlayer?.let { player ->
            if (isPlaying) {
                player.pause()
                isPlaying = false
            } else {
                player.start()
                isPlaying = true
            }
            updatePlayPauseButton()
        }
    }

    private fun updatePlayPauseButton() {
        // Update button icon based on play/pause state
        // You can add play/pause icons here
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}