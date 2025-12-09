package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import android.content.ComponentName
import android.graphics.Bitmap
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.palette.graphics.Palette
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hypersoft.baseproject.core.base.fragment.BaseFragment
import com.hypersoft.baseproject.core.extensions.collectWhenStarted
import com.hypersoft.baseproject.core.extensions.loadAlbumArt
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

    override fun onViewCreated() {
        binding.mbBackMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.TogglePlayPause) }
        binding.mbPreviousMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.SeekToPrevious) }
        binding.mbNextMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.SeekToNext) }
        binding.mbRewindMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Rewind) }
        binding.mbForwardMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.Forward) }
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
                controller?.addListener(playerListener)

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

        // Load album art from current audio
        val currentAudio = state.playlist.getOrNull(state.currentIndex)
        binding.ifvAlbumArtMediaAudioDetail.loadAlbumArt(currentAudio?.id, placeholder = coreR.drawable.ic_svg_audio, error = coreR.drawable.ic_svg_audio)
        
        // Extract and apply vibrant background color from album art
        currentAudio?.id?.let { audioId ->
            extractAndApplyBackgroundColor(audioId)
        } ?: run {
            // Reset to black if no audio
            binding.vBackgroundColorMediaAudioDetail.setBackgroundColor(android.graphics.Color.BLACK)
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

        binding.cpiLoadingMediaAudioDetail.isVisible = state.isLoading
    }

    private fun extractAndApplyBackgroundColor(audioId: Long) {
        // Query album ID from audio ID
        var albumId = 0L
        val projection = arrayOf(android.provider.MediaStore.Audio.Media.ALBUM_ID)
        val selection = "${android.provider.MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(audioId.toString())

        requireContext().contentResolver.query(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val albumIdColumn = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ALBUM_ID)
                albumId = cursor.getLong(albumIdColumn)
            }
        }

        if (albumId == 0L) {
            binding.vBackgroundColorMediaAudioDetail.setBackgroundColor(android.graphics.Color.BLACK)
            return
        }

        // Get album art URI
        val albumArtUri = android.content.ContentUris.withAppendedId(
            android.net.Uri.parse("content://media/external/audio/albumart"),
            albumId
        )

        // Load bitmap and extract palette
        Glide.with(this)
            .asBitmap()
            .load(albumArtUri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        palette?.let {
                            // Get vibrant color, fallback to muted, then dominant
                            val vibrantColor = it.getVibrantColor(
                                it.getMutedColor(
                                    it.getDominantColor(android.graphics.Color.BLACK)
                                )
                            )
                            
                            // Make it slightly darker and more transparent for better text readability
                            val backgroundColor = ColorUtils.setAlphaComponent(
                                ColorUtils.blendARGB(vibrantColor, android.graphics.Color.BLACK, 0.4f),
                                230
                            )
                            
                            binding.vBackgroundColorMediaAudioDetail.setBackgroundColor(backgroundColor)
                        } ?: run {
                            binding.vBackgroundColorMediaAudioDetail.setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    }
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Do nothing
                }
            })
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
        controller?.removeListener(playerListener)
        future?.let { MediaController.releaseFuture(it) }
        controller = null
        future = null
    }

    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            val playerSnapshot = PlayerSnapshot(
                isPlaying = player.isPlaying,
                isLoading = player.playbackState == Player.STATE_BUFFERING,
                title = player.mediaMetadata.title?.toString(),
                artist = player.mediaMetadata.artist?.toString(),
                position = player.currentPosition,
                duration = player.duration,
                currentIndex = player.currentMediaItemIndex
            )

            viewModel.handleIntent(MediaAudioDetailIntent.UpdatePlayerState(playerSnapshot))
        }
    }
}