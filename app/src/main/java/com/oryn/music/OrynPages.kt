package com.oryn.music

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomePage(
    tracks: List<Track>,
    totalTracks: Int,
    favoritesCount: Int,
    loading: Boolean,
    currentTrackId: Long?,
    playing: Boolean,
    onPlay: (Track) -> Unit,
    favorites: Set<Long>,
    onFavorite: (Long) -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 174.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Your music.", color = OrynText, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    if (totalTracks == 0) "Give ORYN access to your audio to get started." else "Everything here comes from this device.",
                    color = OrynMuted,
                    fontSize = 14.sp
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("$totalTracks", "Songs", Modifier.weight(1f))
                StatCard("$favoritesCount", "Favorites", Modifier.weight(1f))
                StatCard("${tracks.map { it.artist }.distinct().size}", "Artists", Modifier.weight(1f))
            }
        }
        item { GlassSectionHeader("Library", "On this device") }
        when {
            loading -> item { LoadingCard() }
            tracks.isEmpty() -> item { EmptyLibraryCard() }
            else -> itemsIndexed(tracks.take(30), key = { _, track -> track.id }) { _, track ->
                TrackRow(
                    track = track,
                    active = track.id == currentTrackId,
                    playing = playing && track.id == currentTrackId,
                    favorite = track.id in favorites,
                    onPlay = { onPlay(track) },
                    onFavorite = { onFavorite(track.id) }
                )
            }
        }
    }
}

@Composable
fun LibraryPage(
    tracks: List<Track>,
    totalTracks: Int,
    loading: Boolean,
    currentTrackId: Long?,
    playing: Boolean,
    favorites: Set<Long>,
    onPlay: (Track) -> Unit,
    onFavorite: (Long) -> Unit,
    onRescan: () -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 174.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Library", color = OrynText, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                    Text("$totalTracks local songs", color = OrynMuted, fontSize = 14.sp)
                }
                IconButton(onClick = onRescan) {
                    Icon(Icons.Outlined.LibraryMusic, "Rescan", tint = OrynMuted)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoPill("${favorites.size} favorites", Icons.Outlined.FavoriteBorder, Modifier.weight(1f))
                InfoPill("Device audio", Icons.Outlined.LibraryMusic, Modifier.weight(1f))
            }
        }
        when {
            loading -> item { LoadingCard() }
            tracks.isEmpty() -> item { EmptyLibraryCard() }
            else -> items(tracks, key = { it.id }) { track ->
                TrackRow(
                    track = track,
                    active = track.id == currentTrackId,
                    playing = playing && track.id == currentTrackId,
                    favorite = track.id in favorites,
                    onPlay = { onPlay(track) },
                    onFavorite = { onFavorite(track.id) }
                )
            }
        }
    }
}

@Composable
fun PlaylistsPage(
    tracks: List<Track>,
    favorites: Set<Long>,
    currentTrackId: Long?,
    playing: Boolean,
    onPlay: (Track) -> Unit,
    onFavorite: (Long) -> Unit
) {
    val favoriteTracks = tracks.filter { it.id in favorites }
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 174.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Playlists", color = OrynText, fontSize = 32.sp, fontWeight = FontWeight.SemiBold)
                Text("Smart collections that stay synced with your library.", color = OrynMuted, fontSize = 14.sp)
            }
        }
        item { PlaylistCard("Favorites", "${favoriteTracks.size} songs you chose", Icons.Outlined.Favorite, OrynAccent) }
        item { PlaylistCard("All songs", "${tracks.size} songs on this device", Icons.Outlined.LibraryMusic, Color(0xFF9BBEFF)) }
        item { GlassSectionHeader("Favorite songs", "Saved locally") }
        if (favoriteTracks.isEmpty()) {
            item { Text("Tap the heart on any song to build this collection.", color = OrynMuted, fontSize = 14.sp, modifier = Modifier.padding(vertical = 20.dp)) }
        } else {
            items(favoriteTracks, key = { it.id }) { track ->
                TrackRow(
                    track = track,
                    active = track.id == currentTrackId,
                    playing = playing && track.id == currentTrackId,
                    favorite = true,
                    onPlay = { onPlay(track) },
                    onFavorite = { onFavorite(track.id) }
                )
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: Track,
    active: Boolean,
    playing: Boolean,
    favorite: Boolean,
    onPlay: () -> Unit,
    onFavorite: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(if (active) Color(0x331E1827) else Color.Transparent)
            .clickable(onClick = onPlay)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkTile(track.id)
        Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
            Text(
                track.title,
                color = if (active) OrynAccent else OrynText,
                fontSize = 15.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${track.artist} • ${track.album} • ${formatDuration(track.durationMs)}",
                color = OrynMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onFavorite) {
            Icon(
                if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                "Favorite",
                tint = if (favorite) OrynAccent else OrynMuted
            )
        }
        Icon(
            if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
            "Play",
            tint = if (active) OrynAccent else OrynMuted,
            modifier = Modifier.size(21.dp)
        )
        Spacer(Modifier.width(6.dp))
    }
}

@Composable
fun ArtworkTile(seed: Long) {
    val gradients = listOf(
        listOf(Color(0xFFB77CFF), Color(0xFF2A1839)),
        listOf(Color(0xFF7CB7FF), Color(0xFF152A44)),
        listOf(Color(0xFFFFA37C), Color(0xFF472016)),
        listOf(Color(0xFF83E2BE), Color(0xFF173B31)),
        listOf(Color(0xFFE59BFF), Color(0xFF352044))
    )
    Box(
        Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(Brush.linearGradient(gradients[(seed % gradients.size).toInt()]))
    ) {
        Box(
            Modifier
                .size(25.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .border(2.dp, Color.White.copy(.52f), CircleShape)
        )
    }
}