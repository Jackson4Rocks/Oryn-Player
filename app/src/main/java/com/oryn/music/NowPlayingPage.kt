package com.oryn.music

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forward10
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay10
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.Shuffle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import kotlinx.coroutines.delay

@Composable
fun NowPlayingPage(
    track: Track,
    playing: Boolean,
    favorite: Boolean,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFavorite: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    player: OrynPlayer
) {
    var position by remember(track.id) { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }

    LaunchedEffect(track.id, playing) {
        while (playing) {
            if (!dragging) {
                position = player.currentPositionMs().toFloat()
            }
            delay(300)
        }
    }

    val maxPosition = track.durationMs.toFloat().coerceAtLeast(1f)

    Box(
        Modifier
            .fillMaxSize()
            .background(OrynBg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x353B2459), Color.Transparent),
                        radius = 850f
                    )
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val artworkSize = (maxWidth * 0.78f).coerceIn(220.dp, 320.dp)

                Box(
                    Modifier
                        .size(artworkSize)
                        .align(Alignment.Center)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(30.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF9C63FA),
                                    Color(0xFF49316D),
                                    Color(0xFF120D18)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(.10f),
                            androidx.compose.foundation.shape.RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .size(artworkSize * .50f)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(0x24000000))
                            .border(
                                2.dp,
                                Color.White.copy(.30f),
                                androidx.compose.foundation.shape.CircleShape
                            )
                    ) {
                        Box(
                            Modifier
                                .size(artworkSize * .22f)
                                .align(Alignment.Center)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .border(
                                    2.dp,
                                    Color.White.copy(.40f),
                                    androidx.compose.foundation.shape.CircleShape
                                )
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    track.title,
                    color = OrynText,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    track.artist,
                    color = OrynAccent,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    track.album,
                    color = OrynMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Slider(
                value = position.coerceIn(0f, maxPosition),
                onValueChange = {
                    dragging = true
                    position = it
                },
                onValueChangeFinished = {
                    dragging = false
                    player.seekTo(position.toLong())
                },
                valueRange = 0f..maxPosition,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatDuration(position.toLong()), color = OrynMuted, fontSize = 11.sp)
                Text(formatDuration(track.durationMs), color = OrynMuted, fontSize = 11.sp)
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallAction(Icons.Outlined.Replay10, "Back 10 seconds", onSeekBack)
                SmallAction(Icons.Outlined.Shuffle, "Shuffle", onShuffle, shuffleEnabled)

                IconButton(onClick = onPrevious) {
                    Icon(
                        Icons.Outlined.SkipPrevious,
                        "Previous",
                        tint = OrynText,
                        modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
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
                    Icon(
                        Icons.Outlined.SkipNext,
                        "Next",
                        tint = OrynText,
                        modifier = Modifier.size(28.dp)
                    )
                }

                SmallAction(Icons.Outlined.Forward10, "Forward 10 seconds", onSeekForward)
                SmallAction(
                    if (repeatMode == Player.REPEAT_MODE_ONE) {
                        Icons.Outlined.RepeatOne
                    } else {
                        Icons.Outlined.Repeat
                    },
                    "Repeat",
                    onRepeat,
                    repeatMode != Player.REPEAT_MODE_OFF
                )
            }

            Text(
                when (repeatMode) {
                    Player.REPEAT_MODE_ONE -> "Repeat: track"
                    Player.REPEAT_MODE_ALL -> "Repeat: queue"
                    else -> "Repeat: off"
                },
                color = OrynMuted,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SmallAction(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    active: Boolean = false
) {
    IconButton(onClick = onClick) {
        Icon(
            icon,
            description,
            tint = if (active) OrynAccent else OrynMuted,
            modifier = Modifier.size(21.dp)
        )
    }
}