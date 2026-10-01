package com.oryn.music

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.launch

val OrynBg = Color(0xFF050506)
val OrynText = Color(0xFFF8F3FA)
val OrynMuted = Color(0xFF96919D)
val OrynSurface = Color(0xFF111015)
val OrynSurface2 = Color(0xFF17151C)
val OrynAccent = Color(0xFFD7B6FF)

@Composable
fun OrynApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var page by rememberSaveable { mutableIntStateOf(0) }
    var aboutOpen by rememberSaveable { mutableStateOf(false) }
    var nowPlayingOpen by rememberSaveable { mutableStateOf(false) }
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var tracks by remember { mutableStateOf<List<Track>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val player = remember { OrynPlayer(context) }
    val preferences = remember { context.getSharedPreferences("oryn_preferences", Context.MODE_PRIVATE) }
    var favorites by remember {
        mutableStateOf(
            preferences.getStringSet("favorites", emptySet())?.mapNotNull { it.toLongOrNull() }?.toSet().orEmpty()
        )
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    fun refresh() {
        scope.launch {
            loading = true
            tracks = MusicScanner.scan(context)
            loading = false
        }
    }

    LaunchedEffect(Unit) { refresh() }

    fun toggleFavorite(id: Long) {
        val next = if (id in favorites) favorites - id else favorites + id
        favorites = next
        preferences.edit { putStringSet("favorites", next.map(Long::toString).toSet()) }
    }

    val currentTrack = player.currentTrackId?.let { id -> tracks.firstOrNull { it.id == id } }

    BackHandler(enabled = nowPlayingOpen) {
        nowPlayingOpen = false
    }

    val filtered = remember(tracks, query) {
        if (query.isBlank()) tracks else tracks.filter {
            it.title.contains(query, true) || it.artist.contains(query, true) || it.album.contains(query, true)
        }
    }

    Box(Modifier.fillMaxSize().background(OrynBg)) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(Color(0x260F0F16), Color.Transparent)),
                radius = size.minDimension * .82f,
                center = androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .08f)
            )
        }

        Column(
            Modifier.fillMaxSize().windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.statusBars)
        ) {
            OrynHeader(
                searchOpen = searchOpen,
                query = query,
                onQueryChange = { query = it },
                onSearchToggle = {
                    searchOpen = !searchOpen
                    if (!searchOpen) query = ""
                },
                onAbout = { aboutOpen = true }
            )

            AnimatedContent(
                targetState = page,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = "oryn-page"
            ) { selectedPage ->
                when (selectedPage) {
                    0 -> HomePage(
                        tracks = filtered,
                        totalTracks = tracks.size,
                        favoritesCount = favorites.size,
                        loading = loading,
                        currentTrackId = player.currentTrackId,
                        playing = player.isPlaying,
                        onPlay = player::toggle,
                        favorites = favorites,
                        onFavorite = ::toggleFavorite
                    )
                    1 -> LibraryPage(
                        tracks = filtered,
                        totalTracks = tracks.size,
                        loading = loading,
                        currentTrackId = player.currentTrackId,
                        playing = player.isPlaying,
                        favorites = favorites,
                        onPlay = player::toggle,
                        onFavorite = ::toggleFavorite,
                        onRescan = ::refresh
                    )
                    else -> PlaylistsPage(
                        tracks = tracks,
                        favorites = favorites,
                        currentTrackId = player.currentTrackId,
                        playing = player.isPlaying,
                        onPlay = player::toggle,
                        onFavorite = ::toggleFavorite
                    )
                }
            }
        }

        if (!nowPlayingOpen && currentTrack != null) {
            MiniPlayer(
                track = currentTrack,
                playing = player.isPlaying,
                onOpen = { nowPlayingOpen = true },
                onPlayPause = { player.toggle(currentTrack) },
                onPrevious = {
                    val index = tracks.indexOfFirst { it.id == currentTrack.id }
                    if (index > 0) player.play(tracks[index - 1])
                },
                onNext = {
                    val index = tracks.indexOfFirst { it.id == currentTrack.id }
                    if (index >= 0 && index + 1 < tracks.size) player.play(tracks[index + 1])
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (!nowPlayingOpen) {
            OrynBottomBar(
                selectedIndex = page,
                onSelected = { page = it },
                onSearch = {
                    searchOpen = true
                    query = ""
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (aboutOpen) {
            AboutOverlay(onDismiss = { aboutOpen = false }, context = context)
        }

        if (nowPlayingOpen && currentTrack != null) {
            NowPlayingPage(
                track = currentTrack,
                playing = player.isPlaying,
                favorite = currentTrack.id in favorites,
                onBack = { nowPlayingOpen = false },
                onPlayPause = { player.toggle(currentTrack) },
                onPrevious = {
                    val index = tracks.indexOfFirst { it.id == currentTrack.id }
                    if (index > 0) player.play(tracks[index - 1])
                },
                onNext = {
                    val index = tracks.indexOfFirst { it.id == currentTrack.id }
                    if (index >= 0 && index + 1 < tracks.size) player.play(tracks[index + 1])
                },
                onFavorite = { toggleFavorite(currentTrack.id) },
                player = player
            )
        }
    }
}