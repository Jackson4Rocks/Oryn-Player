package com.oryn.music

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class OrynPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var controller: MediaController? = null
    private var released = false

    private var queuedTracks: List<Track> = emptyList()

    var currentTrackId by mutableStateOf<Long?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var shuffleEnabled by mutableStateOf(false)
        private set

    var repeatMode by mutableIntStateOf(Player.REPEAT_MODE_OFF)
        private set

    private val controllerFuture = MediaController.Builder(
        appContext,
        SessionToken(
            appContext,
            ComponentName(appContext, OrynPlaybackService::class.java)
        )
    ).buildAsync()

    init {
        controllerFuture.addListener(
            {
                if (released) return@addListener

                runCatching {
                    controller = controllerFuture.get().also { connected ->
                        connected.addListener(object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                this@OrynPlayer.isPlaying = isPlaying
                            }

                            override fun onMediaItemTransition(
                                mediaItem: MediaItem?,
                                reason: Int
                            ) {
                                currentTrackId = mediaItem?.mediaId?.toLongOrNull()
                            }

                            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                                this@OrynPlayer.shuffleEnabled = shuffleModeEnabled
                            }

                            override fun onRepeatModeChanged(repeatMode: Int) {
                                this@OrynPlayer.repeatMode = repeatMode
                            }
                        })

                        shuffleEnabled = connected.shuffleModeEnabled
                        repeatMode = connected.repeatMode
                        isPlaying = connected.isPlaying
                        currentTrackId = connected.currentMediaItem?.mediaId?.toLongOrNull()

                        if (queuedTracks.isNotEmpty()) {
                            applyQueue(queuedTracks, connected)
                        }
                    }
                }
            },
            ContextCompat.getMainExecutor(appContext)
        )
    }

    fun setQueue(tracks: List<Track>) {
        queuedTracks = tracks
        val connected = controller ?: return
        applyQueue(tracks, connected)
    }

    private fun applyQueue(tracks: List<Track>, player: MediaController) {
        val currentId = player.currentMediaItem?.mediaId?.toLongOrNull()
        val currentPosition = player.currentPosition
        val wasPlaying = player.isPlaying

        val sameQueue = player.mediaItemCount == tracks.size &&
            tracks.indices.all { index ->
                player.getMediaItemAt(index).mediaId == tracks[index].id.toString()
            }

        if (sameQueue) {
            return
        }

        val items = tracks.map { track ->
            MediaItem.Builder()
                .setMediaId(track.id.toString())
                .setUri(track.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
        }

        if (items.isEmpty()) {
            player.clearMediaItems()
            return
        }

        val targetIndex = currentId?.let { id ->
            items.indexOfFirst { it.mediaId == id }.takeIf { it >= 0 }
        } ?: 0

        val targetPosition = if (currentId != null) {
            currentPosition.coerceAtLeast(0L)
        } else {
            0L
        }

        player.setMediaItems(items, targetIndex, targetPosition)
        player.prepare()

        if (wasPlaying) {
            player.play()
        }
    }

    fun toggle(track: Track) {
        val connected = controller ?: return

        if (connected.currentMediaItem?.mediaId == track.id.toString()) {
            if (connected.isPlaying) connected.pause() else connected.play()
            return
        }

        play(track)
    }

    fun play(track: Track) {
        val connected = controller ?: return
        val index = queuedTracks.indexOfFirst { it.id == track.id }

        if (index >= 0 && connected.mediaItemCount == queuedTracks.size) {
            connected.seekTo(index, 0L)
            connected.play()
        } else {
            val item = MediaItem.Builder()
                .setMediaId(track.id.toString())
                .setUri(track.uri)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .build()
                )
                .build()
            connected.setMediaItem(item)
            connected.prepare()
            connected.play()
        }
    }

    fun seekTo(positionMs: Long) {
        controller?.let { player ->
            val duration = player.duration.takeIf { it >= 0L } ?: Long.MAX_VALUE
            player.seekTo(positionMs.coerceIn(0L, duration))
        }
    }

    fun seekBy(deltaMs: Long) {
        controller?.let { player ->
            val duration = player.duration.takeIf { it >= 0L } ?: Long.MAX_VALUE
            player.seekTo((player.currentPosition + deltaMs).coerceIn(0L, duration))
        }
    }

    fun toggleShuffle() {
        controller?.let { player ->
            player.shuffleModeEnabled = !player.shuffleModeEnabled
        }
    }

    fun cycleRepeat() {
        controller?.let { player ->
            player.repeatMode = when (player.repeatMode) {
                Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                else -> Player.REPEAT_MODE_OFF
            }
        }
    }

    fun currentPositionMs(): Long = controller?.currentPosition?.coerceAtLeast(0L) ?: 0L

    fun release() {
        if (released) return
        released = true
        controller?.release()
        MediaController.releaseFuture(controllerFuture)
        scope.cancel()
    }
}
