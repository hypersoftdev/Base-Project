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
import com.hypersoft.baseproject.core.extensions.loadAlbumArtWithGradientBackground
import com.hypersoft.baseproject.core.extensions.popFrom
import com.hypersoft.baseproject.core.extensions.showToast
import com.hypersoft.baseproject.core.extensions.toTimeFormat
import com.hypersoft.baseproject.presentation.R
import com.hypersoft.baseproject.presentation.databinding.FragmentMediaAudioDetailBinding
import com.hypersoft.baseproject.presentation.mediaAudioDetails.effect.MediaAudioDetailEffect
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.MediaAudioDetailIntent
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.PlayerSnapshot
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
 * - Observes state from ViewModel and renders UI
 * - Sends user actions as intents to ViewModel
 * - Handles effects from ViewModel (one-time actions)
 * - Forwards player events as intents to ViewModel
 */
@UnstableApi
class MediaAudioDetailFragment : BaseFragment<FragmentMediaAudioDetailBinding>(FragmentMediaAudioDetailBinding::inflate) {

    private val navArgs: MediaAudioDetailFragmentArgs by navArgs()
    private val viewModel: MediaAudioDetailViewModel by viewModel()

    private var controller: MediaController? = null
    private var future: ListenableFuture<MediaController>? = null
    private var backgroundJob: Job? = null
    private var positionUpdateJob: Job? = null
    private var lastAudioId: Long? = null

    override fun onViewCreated() {
        binding.mbBackMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.TogglePlayPause) }
        binding.mbPreviousMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.SeekToPrevious) }
        binding.mbNextMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.SeekToNext) }
        binding.mbRewindMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Rewind) }
        binding.mbForwardMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Forward) }
        binding.mbRepeatMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Repeat) }
        binding.mbShuffleMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Shuffle) }
        binding.sliderMediaAudioDetail.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.handleIntent(MediaAudioDetailIntent.SeekTo(value.toLong()))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connectController()
    }

    override fun initObservers() {
        observeState()
        observeEffect()
    }

    private fun connectController() {
        val ctx = context ?: return
        val token = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))

        future = MediaController.Builder(ctx, token).buildAsync()
        future?.addListener(
            {
                controller = future?.get()
                controller?.let { ctrl ->
                    ctrl.addListener(playerListener)

                    // Capture initial state including repeat/shuffle
                    val initialSnapshot = PlayerSnapshot(
                        isPlaying = ctrl.isPlaying,
                        isLoading = ctrl.playbackState == Player.STATE_BUFFERING,
                        title = ctrl.mediaMetadata.title?.toString(),
                        artist = ctrl.mediaMetadata.artist?.toString(),
                        position = ctrl.currentPosition,
                        duration = ctrl.duration,
                        currentIndex = ctrl.currentMediaItemIndex,
                        repeatMode = ctrl.repeatMode,
                        shuffleModeEnabled = ctrl.shuffleModeEnabled
                    )
                    viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(initialSnapshot))
                }
                startPositionUpdates()

                // Request playlist load
                viewModel.handleIntent(MediaAudioDetailIntent.LoadPlaylist(navArgs.audioUriPath))
            },
            MoreExecutors.directExecutor()
        )
    }

    private fun observeState() {
        collectWhenStarted(viewModel.state) { state -> render(state) }
    }

    private fun observeEffect() {
        collectWhenStarted(viewModel.effect) { effect -> handleEffect(effect) }
    }

    private fun render(state: MediaAudioDetailState) {
        // Setup playlist when ready
        controller?.let { controller ->
            if (state.playlist.isNotEmpty() && controller.mediaItemCount == 0) {
                val items = state.playlist.map { audio ->
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

                controller.setMediaItems(items, state.currentIndex, 0)
                controller.prepare()
                controller.play()
            }
        }

        // Render UI state
        binding.mtvTitleMediaAudioDetail.text = state.title
        binding.mtvArtistMediaAudioDetail.text = state.artist

        // Load album art only when audioId changes (optimization)
        val currentAudio = state.playlist.getOrNull(state.currentIndex)
        val currentAudioId = currentAudio?.id
        if (currentAudioId != lastAudioId) {
            lastAudioId = currentAudioId

            backgroundJob?.cancel()
            backgroundJob = lifecycleScope.launch {
                binding.ifvAlbumArtMediaAudioDetail.loadAlbumArtWithGradientBackground(
                    backgroundView = binding.vBackgroundColorMediaAudioDetail,
                    audioId = currentAudioId,
                    placeholder = coreR.drawable.img_png_placeholder,
                    error = coreR.drawable.img_png_placeholder
                )
            }
        }

        // Update slider only when duration is valid (> 0)
        if (state.duration > 0) {
            binding.sliderMediaAudioDetail.valueTo = state.duration.toFloat()
            binding.sliderMediaAudioDetail.value = state.currentPosition.toFloat().coerceIn(0f, state.duration.toFloat())
        }

        binding.mtvCurrentProgressMediaAudioDetail.text = state.currentPosition.toTimeFormat()
        if (state.duration > 0) {
            binding.mtvTotalTimeMediaAudioDetail.text = state.duration.toTimeFormat()
        }

        binding.mbPlayPauseMediaAudioDetail.setIconResource(
            if (state.isPlaying) coreR.drawable.ic_svg_pause else coreR.drawable.ic_svg_play
        )

        // Update repeat button icon based on repeat mode
        binding.mbRepeatMediaAudioDetail.setIconResource(
            when (state.repeatMode) {
                Player.REPEAT_MODE_ONE -> coreR.drawable.ic_svg_media_repeat_one
                Player.REPEAT_MODE_ALL -> coreR.drawable.ic_svg_media_repeat_on
                else -> coreR.drawable.ic_svg_media_repeat_off
            }
        )

        // Update shuffle button icon based on shuffle mode
        binding.mbShuffleMediaAudioDetail.setIconResource(
            when (state.shuffleModeEnabled) {
                true -> coreR.drawable.ic_svg_media_shuffle_on
                false -> coreR.drawable.ic_svg_media_shuffle_off
            }
        )

        binding.cpiLoadingMediaAudioDetail.isVisible = state.isLoading
    }

    private fun handleEffect(effect: MediaAudioDetailEffect) {
        when (effect) {
            is MediaAudioDetailEffect.NavigateBack -> popFrom(R.id.mediaAudioDetailFragment)
            is MediaAudioDetailEffect.Play -> controller?.play()
            is MediaAudioDetailEffect.Pause -> controller?.pause()
            is MediaAudioDetailEffect.SeekToNext -> controller?.seekToNextMediaItem()
            is MediaAudioDetailEffect.SeekToPrevious -> controller?.seekToPreviousMediaItem()
            is MediaAudioDetailEffect.SeekTo -> controller?.seekTo(effect.positionMs)
            is MediaAudioDetailEffect.ShowError -> context?.showToast(effect.message)
            is MediaAudioDetailEffect.Shuffle -> controller?.let { ctrl -> ctrl.shuffleModeEnabled = !ctrl.shuffleModeEnabled }
            is MediaAudioDetailEffect.Repeat -> {
                controller?.let { ctrl ->
                    // Cycle through repeat modes: OFF -> ALL -> ONE -> OFF
                    val nextRepeatMode = when (ctrl.repeatMode) {
                        Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                        Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                        Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                        else -> Player.REPEAT_MODE_OFF
                    }
                    ctrl.repeatMode = nextRepeatMode
                }
            }

            is MediaAudioDetailEffect.Rewind -> {
                controller?.let { ctrl ->
                    val newPosition = (ctrl.currentPosition - 5000).coerceAtLeast(0)
                    ctrl.seekTo(newPosition)
                }
            }

            is MediaAudioDetailEffect.Forward -> {
                controller?.let { ctrl ->
                    val duration = ctrl.duration
                    if (duration != C.TIME_UNSET) {
                        val newPosition = (ctrl.currentPosition + 15000).coerceAtMost(duration)
                        ctrl.seekTo(newPosition)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        disconnectController()
    }

    private fun disconnectController() {
        stopPositionUpdates()
        controller?.removeListener(playerListener)
        future?.let { MediaController.releaseFuture(it) }
        controller = null
        future = null
    }

    private fun startPositionUpdates() {
        stopPositionUpdates()
        positionUpdateJob = lifecycleScope.launch {
            while (isActive) {
                controller?.let { ctrl ->
                    if (ctrl.isPlaying && ctrl.duration != C.TIME_UNSET) {
                        val snapshot = PlayerSnapshot(
                            isPlaying = ctrl.isPlaying,
                            isLoading = ctrl.playbackState == Player.STATE_BUFFERING,
                            title = ctrl.mediaMetadata.title?.toString(),
                            artist = ctrl.mediaMetadata.artist?.toString(),
                            position = ctrl.currentPosition,
                            duration = ctrl.duration,
                            currentIndex = ctrl.currentMediaItemIndex,
                            repeatMode = ctrl.repeatMode,
                            shuffleModeEnabled = ctrl.shuffleModeEnabled
                        )
                        viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(snapshot))
                    }
                }
                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private val playerListener = object : Player.Listener {
        override fun onRepeatModeChanged(repeatMode: Int) {
            // Update state when repeat mode changes
            controller?.let { ctrl ->
                val snapshot = PlayerSnapshot(
                    isPlaying = ctrl.isPlaying,
                    isLoading = ctrl.playbackState == Player.STATE_BUFFERING,
                    title = ctrl.mediaMetadata.title?.toString(),
                    artist = ctrl.mediaMetadata.artist?.toString(),
                    position = ctrl.currentPosition,
                    duration = ctrl.duration,
                    currentIndex = ctrl.currentMediaItemIndex,
                    repeatMode = ctrl.repeatMode,
                    shuffleModeEnabled = ctrl.shuffleModeEnabled
                )
                viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(snapshot))
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            // Update state when shuffle mode changes
            controller?.let { ctrl ->
                val snapshot = PlayerSnapshot(
                    isPlaying = ctrl.isPlaying,
                    isLoading = ctrl.playbackState == Player.STATE_BUFFERING,
                    title = ctrl.mediaMetadata.title?.toString(),
                    artist = ctrl.mediaMetadata.artist?.toString(),
                    position = ctrl.currentPosition,
                    duration = ctrl.duration,
                    currentIndex = ctrl.currentMediaItemIndex,
                    repeatMode = ctrl.repeatMode,
                    shuffleModeEnabled = ctrl.shuffleModeEnabled
                )
                viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(snapshot))
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            val playerSnapshot = PlayerSnapshot(
                isPlaying = player.isPlaying,
                isLoading = player.playbackState == Player.STATE_BUFFERING,
                title = player.mediaMetadata.title?.toString(),
                artist = player.mediaMetadata.artist?.toString(),
                position = player.currentPosition,
                duration = player.duration,
                currentIndex = player.currentMediaItemIndex,
                repeatMode = player.repeatMode,
                shuffleModeEnabled = player.shuffleModeEnabled
            )

            viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(playerSnapshot))
        }
    }
}