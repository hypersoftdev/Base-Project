package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import android.content.ComponentName
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
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudioDetailBinding
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.player.PlaybackService
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.hypersoft.baseproject.core.R as coreR

/**
 * Fragment for audio playback using Media3 MediaController.
 * Follows MVI pattern:
 * - Observes state from ViewModel
 * - Forwards user actions as intents
 * - Forwards player events as intents
 * - Renders UI from state
 */
@UnstableApi
class MediaAudioDetailFragment : BaseFragment<FragmentMediaAudioDetailBinding>(FragmentMediaAudioDetailBinding::inflate) {

    private val navArgs: MediaAudioDetailFragmentArgs by navArgs()
    private val viewModel: MediaAudioDetailViewModel by viewModel()

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var progressUpdateJob: Job? = null
    private var playlistLoaded = false

    override fun onViewCreated() {
        setupClickListeners()
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
        observeState()
        observeEffect()
    }

    private fun setupClickListeners() {
        binding.mbBackMediaAudioDetail.setOnClickListener {
            viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack)
        }

        binding.mbPlayPauseMediaAudioDetail.setOnClickListener {
            mediaController?.let { controller ->
                if (controller.isPlaying) {
                    controller.pause()
                } else {
                    controller.play()
                }
            }
        }

        binding.mbPreviousMediaAudioDetail.setOnClickListener {
            mediaController?.seekToPreviousMediaItem()
        }

        binding.mbNextMediaAudioDetail.setOnClickListener {
            mediaController?.seekToNextMediaItem()
        }

        binding.mbRewindMediaAudioDetail.setOnClickListener {
            mediaController?.let { controller ->
                val newPosition = (controller.currentPosition - 5000).coerceAtLeast(0)
                controller.seekTo(newPosition)
            }
        }

        binding.mbForwardMediaAudioDetail.setOnClickListener {
            mediaController?.let { controller ->
                val duration = controller.duration
                if (duration != C.TIME_UNSET) {
                    val newPosition = (controller.currentPosition + 15000).coerceAtMost(duration)
                    controller.seekTo(newPosition)
                }
            }
        }

        binding.sliderMediaAudioDetail.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                mediaController?.seekTo(value.toLong())
            }
        }
    }

    private fun initializeController() {
        val context = requireContext()
        val sessionToken = SessionToken(
            context,
            ComponentName(context, PlaybackService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener(
            {
                try {
                    mediaController = controllerFuture?.get()
                    mediaController?.let { controller ->
                        setupPlayerListener(controller)
                        loadPlaylist()
                    }
                } catch (e: Exception) {
                    context.showToast("Failed to connect to playback service: ${e.message}")
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun setupPlayerListener(controller: MediaController) {
        controller.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(isPlaying = isPlaying))
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isLoading = playbackState == Player.STATE_BUFFERING
                viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(isLoading = isLoading))
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let { item ->
                    val metadata = item.mediaMetadata
                    val title = metadata.title?.toString() ?: ""
                    val artist = metadata.artist?.toString() ?: ""

                    viewModel.handleIntent(
                        MediaAudioDetailIntent.UpdatePlayerState(
                            title = title,
                            artist = artist
                        )
                    )

                    // Update current index
                    val currentIndex = controller.currentMediaItemIndex
                    viewModel.handleIntent(MediaAudioDetailIntent.OnMediaItemTransition(currentIndex))
                }
            }

            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                    events.contains(Player.EVENT_TIMELINE_CHANGED) ||
                    events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                    events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    updateProgressFromPlayer(player)
                }
            }
        })

        // Initial state update
        updateProgressFromPlayer(controller)
        updateMetadataFromMediaItem(controller.currentMediaItem)

        // Start periodic progress updates
        startProgressUpdates(controller)
    }

    private fun loadPlaylist() {
        // Request playlist load from ViewModel
        viewModel.handleIntent(MediaAudioDetailIntent.LoadPlaylist(navArgs.audioUriPath))
    }

    private fun setupPlaylistIfReady(controller: MediaController, state: MediaAudioDetailState) {
        if (state.playlist.isNotEmpty() && state.currentIndex >= 0 && !playlistLoaded) {
            playlistLoaded = true

            val mediaItems = state.playlist.map { audio ->
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

            // Set playlist and prepare
            controller.setMediaItems(mediaItems, state.currentIndex.coerceIn(0, mediaItems.size - 1), 0)
            controller.prepare()
            controller.play()
        }
    }

    private fun updateMetadataFromMediaItem(mediaItem: MediaItem?) {
        mediaItem?.mediaMetadata?.let { metadata ->
            viewModel.handleIntent(
                MediaAudioDetailIntent.UpdatePlayerState(
                    title = metadata.title?.toString() ?: "",
                    artist = metadata.artist?.toString() ?: ""
                )
            )
        }
    }

    private fun updateProgressFromPlayer(player: Player) {
        val duration = if (player.duration != C.TIME_UNSET) player.duration else 0L
        val position = player.currentPosition

        viewModel.handleIntent(
            MediaAudioDetailIntent.UpdatePlayerState(
                currentPosition = position,
                duration = duration
            )
        )
    }

    private fun startProgressUpdates(controller: MediaController) {
        stopProgressUpdates()
        progressUpdateJob = lifecycleScope.launch {
            while (isActive) {
                if (controller.isPlaying) {
                    updateProgressFromPlayer(controller)
                    delay(100)
                } else {
                    delay(500)
                }
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun releaseController() {
        stopProgressUpdates()
        controllerFuture?.let { future ->
            MediaController.releaseFuture(future)
        }
        mediaController = null
        controllerFuture = null
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: MediaAudioDetailState) {
        // Setup playlist if ready and controller is available
        mediaController?.let { controller ->
            setupPlaylistIfReady(controller, state)
        }

        // Update play/pause button
        binding.mbPlayPauseMediaAudioDetail.setIconResource(
            if (state.isPlaying) coreR.drawable.ic_svg_pause else coreR.drawable.ic_svg_play
        )

        // Update title and artist
        binding.mtvTitleMediaAudioDetail.text = state.title
        binding.mtvArtistMediaAudioDetail.text = state.artist

        // Update progress slider
        if (state.duration > 0) {
            binding.sliderMediaAudioDetail.valueTo = state.duration.toFloat()
            binding.sliderMediaAudioDetail.value = state.currentPosition.toFloat()
        }

        // Update time labels
        binding.mtvCurrentProgressMediaAudioDetail.text = state.currentPosition.toTimeFormat()
        if (state.duration > 0) {
            binding.mtvTotalTimeMediaAudioDetail.text = state.duration.toTimeFormat()
        }

        // Update loading indicator
        binding.cpiLoadingMediaAudioDetail.isVisible = state.isLoading

        // Show error if any
        state.error?.let { error ->
            context?.showToast(error)
        }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect ->
            handleEffect(effect)
        }
    }

    private fun handleEffect(effect: MediaAudioDetailEffect) {
        when (effect) {
            is MediaAudioDetailEffect.NavigateBack -> popFrom(R.id.mediaAudioDetailFragment)
            is MediaAudioDetailEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}