package com.hypersoft.baseproject.presentation.mediaAudioDetails.ui

import android.content.ComponentName
import androidx.core.view.isVisible
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
import com.hypersoft.baseproject.presentation.mediaAudioDetails.intent.PlayerSnapshot
import com.hypersoft.baseproject.presentation.mediaAudioDetails.player.PlaybackService
import com.hypersoft.baseproject.presentation.mediaAudioDetails.state.MediaAudioDetailState
import com.hypersoft.baseproject.presentation.mediaAudioDetails.viewModel.MediaAudioDetailViewModel
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

    private var controller: MediaController? = null
    private var future: ListenableFuture<MediaController>? = null

    override fun onViewCreated() {
        binding.mbBackMediaAudioDetail.setOnClickListener { viewModel.handleIntent(MediaAudioDetailIntent.NavigateBack) }
        binding.mbPlayPauseMediaAudioDetail.setOnClickListener { controller?.let { if (it.isPlaying) it.pause() else it.play() } }
        binding.mbPreviousMediaAudioDetail.setOnClickListener { controller?.seekToPrevious() }
        binding.mbNextMediaAudioDetail.setOnClickListener { controller?.seekToNext() }
        binding.mbRewindMediaAudioDetail.setOnClickListener { controller?.seekTo(controller!!.currentPosition - 5000) }
        binding.mbForwardMediaAudioDetail.setOnClickListener { controller?.seekTo(controller!!.currentPosition + 10000) }
        binding.sliderMediaAudioDetail.addOnChangeListener { _, value, fromUser -> if (fromUser) controller?.seekTo(value.toLong()) }
    }

    override fun onStart() {
        super.onStart()
        connectController()
    }

    override fun onStop() {
        super.onStop()
        disconnectController()
    }

    override fun initObservers() {
        collectWhenStarted(viewModel.state) { render(it) }
        collectWhenStarted(viewModel.effect) { handleEffect(it) }
    }

    private fun connectController() {
        val ctx = context ?: return
        val token = SessionToken(ctx, ComponentName(ctx, PlaybackService::class.java))

        future = MediaController.Builder(ctx, token).buildAsync()
        future?.addListener(
            {
                controller = future?.get()
                controller?.addListener(playerListener)

                // Ask ViewModel to load playlist
                viewModel.handleIntent(MediaAudioDetailIntent.LoadPlaylist(navArgs.audioUriPath))
            },
            MoreExecutors.directExecutor()
        )
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

            viewModel.handleIntent(MediaAudioDetailIntent.PlayerSnapshot(playerSnapshot))
        }
    }

    private fun render(state: MediaAudioDetailState) {
        // Playlist setup
        controller?.let { controller ->
            if (state.playlist.isNotEmpty() && controller.mediaItemCount == 0) {

                val items = state.playlist.map { audio ->
                    val metaData = MediaMetadata.Builder().setTitle(audio.displayName).setArtist(audio.artist).build()
                    MediaItem.Builder().setUri(audio.uri).setMediaMetadata(metaData).build()
                }

                controller.setMediaItems(items, state.currentIndex, 0)
                controller.prepare()
                controller.play()
            }
        }

        // UI rendering
        binding.mtvTitleMediaAudioDetail.text = state.title
        binding.mtvArtistMediaAudioDetail.text = state.artist
        binding.sliderMediaAudioDetail.valueTo = state.duration.toFloat()
        binding.sliderMediaAudioDetail.value = state.currentPosition.toFloat()

        binding.mtvCurrentProgressMediaAudioDetail.text = state.currentPosition.toTimeFormat()
        binding.mtvTotalTimeMediaAudioDetail.text = state.duration.toTimeFormat()

        binding.mbPlayPauseMediaAudioDetail.setIconResource(if (state.isPlaying) coreR.drawable.ic_svg_pause else coreR.drawable.ic_svg_play)

        binding.cpiLoadingMediaAudioDetail.isVisible = state.isLoading
    }

    private fun handleEffect(effect: MediaAudioDetailEffect) {
        when (effect) {
            is MediaAudioDetailEffect.NavigateBack -> popFrom(R.id.mediaAudioDetailFragment)
            is MediaAudioDetailEffect.ShowError -> context?.showToast(effect.message)
        }
    }
}