package com.oryn.music

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OrynPlayer(private val context: Context) {
    var currentTrackId by mutableStateOf<Long?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    private var mediaPlayer: MediaPlayer? = null

    fun toggle(track: Track) {
        if (currentTrackId == track.id && mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
            } else {
                mediaPlayer?.start()
                isPlaying = true
            }
            return
        }
        play(track)
    }

    fun play(track: Track) {
        releasePlayer()

        val newPlayer = MediaPlayer()
        mediaPlayer = newPlayer
        currentTrackId = track.id
        isPlaying = false

        newPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        try {
            newPlayer.setDataSource(context, track.uri)
            newPlayer.setOnPreparedListener {
                it.start()
                isPlaying = true
            }
            newPlayer.setOnCompletionListener {
                isPlaying = false
            }
            newPlayer.setOnErrorListener { _, _, _ ->
                isPlaying = false
                true
            }
            newPlayer.prepareAsync()
        } catch (_: Exception) {
            releasePlayer()
        }
    }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.let { player ->
            runCatching { player.seekTo(positionMs.coerceIn(0L, player.duration.toLong()).toInt()) }
        }
    }

    fun currentPositionMs(): Long =
        mediaPlayer?.let { runCatching { it.currentPosition.toLong() }.getOrDefault(0L) } ?: 0L

    fun release() {
        releasePlayer()
    }

    private fun releasePlayer() {
        mediaPlayer?.runCatching { reset() }
        mediaPlayer?.runCatching { release() }
        mediaPlayer = null
        isPlaying = false
        currentTrackId = null
    }
}