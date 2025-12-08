package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import android.content.ComponentName
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.navArgs
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.constants.Constants.TAG
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudioDetailBinding
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.player.PlaybackService
import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

/**
 * Fragment that uses MediaController to connect to PlaybackService.
 * Follows Media3 best practices:
 * - Creates MediaController in onStart()
 * - Uses controller callbacks for UI updates
 * - Connects buttons directly to controller methods
 * - Releases controller in onStop()
 */
@UnstableApi
class MediaAudioDetailFragment : BaseFragment<FragmentMediaAudioDetailBinding>(FragmentMediaAudioDetailBinding::inflate) {

    private val navArgs: MediaAudioDetailFragmentArgs by navArgs()
    private val viewModel: MediaAudioDetailViewModel by viewModel()

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private var progressUpdateHandler: Handler? = null
    private var progressUpdateRunnable: Runnable? = null

    private var isUpdatingSlider = false

    override fun onViewCreated() {
        binding.mbBackMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { onPlayPause() }
        binding.mbPreviousMediaAudioDetail.setOnClickListener { mediaController?.seekToPreviousMediaItem() }
        binding.mbNextMediaAudioDetail.setOnClickListener { mediaController?.seekToNextMediaItem() }
        binding.mbRewindMediaAudioDetail.setOnClickListener { onRewindClick(milliseconds = 5000) }
        binding.mbForwardMediaAudioDetail.setOnClickListener { onForwardClick(milliseconds = 1500) }
        binding.sliderMediaAudioDetail.addOnChangeListener { _, value, fromUser -> onSliderChange(fromUser, value) }
    }

    override fun onStart() {
        super.onStart()
        initializeController()
    }

    override fun onStop() {
        super.onStop()
        releaseController()
    }

    override fun initObservers() {
        observeEffect()
    }

    private fun initializeController() {
        // Create SessionToken for PlaybackService
        val ctx = context ?: return
        val sessionToken = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))

        // Build MediaController asynchronously
        mediaControllerFuture = MediaController.Builder(ctx, sessionToken).buildAsync()
        mediaControllerFuture?.addListener(
            {
                try {
                    mediaController = mediaControllerFuture?.get()
                    mediaController?.let { ctrl ->
                        setupControllerListeners(ctrl)
                        loadPlaylist(ctrl)
                    }
                } catch (ex: Exception) {
                    Log.e(TAG, "MediaAudioDetailFragment: initializeController: Exception: ", ex)
                    ctx.showToast("Failed to connect to playback service: ${ex.message}")
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun setupControllerListeners(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlayPauseButton(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                updateLoadingState(playbackState == Player.STATE_BUFFERING)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateMetadata(mediaItem)
            }

            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                    events.contains(Player.EVENT_TIMELINE_CHANGED) ||
                    events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                    events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    updateProgress()
                }
            }
        })

        // Start periodic progress updates
        startProgressUpdates(controller)
    }

    private fun loadPlaylist(controller: MediaController) {
        lifecycleScope.launch {
            try {
                val (audios, startIndex) = viewModel.loadPlaylist(navArgs.audioUriPath)

                // Convert AudioEntity list to MediaItem list
                val mediaItems = audios.map { audio ->
                    MediaItem.fromUri(audio.uri)
                        .buildUpon()
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(audio.displayName)
                                .setArtist(audio.artist)
                                .setAlbumTitle(audio.album)
                                .build()
                        )
                        .build()
                }

                // Use MediaController to set the playlist - this automatically syncs with the service
                controller.setMediaItems(mediaItems, startIndex.coerceIn(0, mediaItems.size - 1), 0)
                controller.prepare()
                controller.play()

            } catch (e: Exception) {
                context?.showToast("Failed to load playlist: ${e.message}")
            }
        }
    }

    private fun startProgressUpdates(controller: MediaController) {
        stopProgressUpdates() // Stop any existing updates

        progressUpdateHandler = Handler(Looper.getMainLooper())
        progressUpdateRunnable = object : Runnable {
            override fun run() {
                if (controller.isPlaying) {
                    updateProgress()
                    progressUpdateHandler?.postDelayed(this, 100) // Update every 100ms
                } else {
                    progressUpdateHandler?.postDelayed(this, 500) // Update every 500ms when paused
                }
            }
        }
        progressUpdateHandler?.post(progressUpdateRunnable!!)
    }

    private fun stopProgressUpdates() {
        progressUpdateRunnable?.let { progressUpdateHandler?.removeCallbacks(it) }
        progressUpdateRunnable = null
        progressUpdateHandler = null
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        binding.mbPlayPauseMediaAudioDetail.setIconResource(if (isPlaying) coreR.drawable.ic_svg_pause else coreR.drawable.ic_svg_play)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.cpiLoadingMediaAudioDetail.isVisible = isLoading
    }

    private fun updateMetadata(mediaItem: MediaItem?) {
        mediaItem?.mediaMetadata?.let { metadata ->
            binding.mtvTitleMediaAudioDetail.text = metadata.title?.toString() ?: ""
            binding.mtvArtistMediaAudioDetail.text = metadata.artist?.toString() ?: ""
        }
    }

    private fun updateProgress() {
        mediaController?.let { ctrl ->
            val duration = if (ctrl.duration != C.TIME_UNSET) {
                ctrl.duration.toInt()
            } else {
                0
            }
            val position = ctrl.currentPosition.toInt()

            isUpdatingSlider = true

            if (duration > 0) {
                binding.sliderMediaAudioDetail.valueTo = duration.toFloat()
                binding.sliderMediaAudioDetail.value = position.toFloat()
            }
            isUpdatingSlider = false

            binding.mtvCurrentProgressMediaAudioDetail.text = position.toLong().toTimeFormat()
            if (duration > 0) {
                binding.mtvTotalTimeMediaAudioDetail.text = duration.toLong().toTimeFormat()
            }
        }
    }

    private fun releaseController() {
        stopProgressUpdates()
        mediaControllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }
        mediaController = null
        mediaControllerFuture = null
    }

    private fun onPlayPause() {
        mediaController?.let { ctrl ->
            when (ctrl.isPlaying) {
                true -> ctrl.pause()
                false -> ctrl.play()
            }
        }
    }

    private fun onRewindClick(milliseconds: Int = 10_000) {
        mediaController?.let { ctrl ->
            val newPosition = (ctrl.currentPosition - milliseconds).coerceAtLeast(0)
            ctrl.seekTo(newPosition)
        }
    }

    private fun onForwardClick(milliseconds: Int = 10_000) {
        mediaController?.let { ctrl ->
            val duration = ctrl.duration
            if (duration != C.TIME_UNSET) {
                val newPosition = (ctrl.currentPosition + milliseconds).coerceAtMost(duration)
                ctrl.seekTo(newPosition)
            }
        }
    }

    private fun onSliderChange(fromUser: Boolean, value: Float) {
        if (fromUser && !isUpdatingSlider) {
            mediaController?.seekTo(value.toLong())
        }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect ->
            when (effect) {
                is MediaAudioDetailEffect.NavigateBack -> popFrom(R.id.mediaAudioDetailFragment)
                is MediaAudioDetailEffect.ShowError -> context?.showToast(effect.message)
            }
        }
    }
}