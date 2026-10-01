package com.oryn.music

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun NowPlayingPage(
    track: Track,
    playing: Boolean,
    favorite: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFavorite: () -> Unit,
    player: OrynPlayer
) {
    var position by remember(track.id) { mutableFloatStateOf(0f) }

    LaunchedEffect(track.id, playing) {
        while (playing) {
            position = player.currentPositionMs().toFloat()
            delay(400)
        }
    }

    val maxPosition = track.durationMs.toFloat().coerceAtLeast(1f)

    Box(
        Modifier
            .fillMaxSize()
            .background(OrynBg)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x403B2459), Color.Transparent),
                        radius = 900f
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = OrynText)
                }
                Text(
                    "NOW PLAYING",
                    color = OrynMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.2.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        "Favorite",
                        tint = if (favorite) OrynAccent else OrynText
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(330.dp)
                        .clip(RoundedCornerShape(34.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFA16DFF),
                                    Color(0xFF3E2858),
                                    Color(0xFF120E18)
                                )
                            )
                        )
                        .border(1.dp, Color.White.copy(.10f), RoundedCornerShape(34.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(164.dp)
                            .clip(CircleShape)
                            .background(Color(0x24000000))
                            .border(2.dp, Color.White.copy(.32f), CircleShape)
                    ) {
                        Box(
                            Modifier
                                .size(74.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .border(2.dp, Color.White.copy(.42f), CircleShape)
                        )
                    }
                }

                Spacer(Modifier.height(26.dp))
                Text(
                    track.title,
                    color = OrynText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(track.artist, color = OrynAccent, fontSize = 14.sp)
                Text(track.album, color = OrynMuted, fontSize = 12.sp)

                Spacer(Modifier.height(24.dp))
                Slider(
                    value = position.coerceIn(0f, maxPosition),
                    onValueChange = { position = it },
                    onValueChangeFinished = { player.seekTo(position.toLong()) },
                    valueRange = 0f..maxPosition
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatDuration(position.toLong()), color = OrynMuted, fontSize = 11.sp)
                    Text(formatDuration(track.durationMs), color = OrynMuted, fontSize = 11.sp)
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Outlined.SkipPrevious, "Previous", tint = OrynText, modifier = Modifier.size(30.dp))
                }
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(OrynAccent)
                ) {
                    Icon(
                        if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        "Play or pause",
                        tint = Color(0xFF140E1A),
                        modifier = Modifier.size(34.dp)
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Outlined.SkipNext, "Next", tint = OrynText, modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}