package com.oryn.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.oryn.music.ui.theme.OrynTheme

private val Bg = Color(0xFF070709)
private val TextPrimary = Color(0xFFF8F3FA)
private val TextMuted = Color(0xFF9C95A2)
private val Accent = Color(0xFFD9B9FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent { OrynTheme { OrynApp() } }
    }
}

data class NavItem(val label: String, val icon: ImageVector)
data class Track(val title: String, val artist: String, val album: String)

private val navItems = listOf(
    NavItem("Home", Icons.Outlined.Home),
    NavItem("Library", Icons.Outlined.LibraryMusic),
    NavItem("Playlists", Icons.Outlined.QueueMusic)
)

private val demoTracks = listOf(
    Track("Midnight Signals", "Oryn Radio", "Afterglow"),
    Track("Static Hearts", "Velvet Frame", "Nocturne"),
    Track("Slow Orbit", "Astra Vale", "Distance"),
    Track("Glassline", "North Bloom", "Fragments")
)

@Composable
private fun OrynApp() {
    var selected by remember { mutableIntStateOf(0) }
    var playing by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1800FFFFFF), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.13f),
                    radius = size.minDimension * 0.72f
                ),
                radius = size.minDimension * 0.72f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.13f)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 154.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            item { TopBar() }
            item {
                AnimatedContent(
                    targetState = selected,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(120)) },
                    label = "page-transition"
                ) { page ->
                    when (page) {
                        0 -> HomePage(onPlay = { playing = it })
                        1 -> LibraryPage(onPlay = { playing = it })
                        else -> PlaylistPage()
                    }
                }
            }
        }

        MiniPlayer(
            track = demoTracks[playing],
            onNext = { playing = (playing + 1) % demoTracks.size },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 94.dp)
                .padding(horizontal = 18.dp)
        )

        LiquidGlassNavBar(
            selectedIndex = selected,
            onSelected = { selected = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 14.dp, end = 14.dp, bottom = 12.dp)
        )
    }
}

@Composable
private fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "ORYN",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                "LOCAL MUSIC",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.2.sp
            )
        }
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextPrimary)
        }
    }
}

@Composable
private fun HomePage(onPlay: (Int) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
        Text("Good evening.", color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(5.dp))
        Text(
            "Made for your ears.",
            color = TextPrimary,
            fontSize = 31.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.8).sp
        )
        Spacer(Modifier.height(24.dp))

        HeroArtwork()
        Spacer(Modifier.height(20.dp))
        Text("Recently played", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        demoTracks.forEachIndexed { index, track ->
            TrackRow(track, index, onClick = { onPlay(index) })
        }
    }
}

@Composable
private fun LibraryPage(onPlay: (Int) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
        Text("Your library", color = TextPrimary, fontSize = 31.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(7.dp))
        Text("Everything stored on this device.", color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        listOf(
            "Songs" to Icons.Outlined.LibraryMusic,
            "Albums" to Icons.Outlined.Album,
            "Artists" to Icons.Outlined.Home,
            "Favorites" to Icons.Outlined.FavoriteBorder
        ).forEach { (label, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(Color(0xFF131217)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Accent)
                }
                Text(label, Modifier.padding(start = 14.dp), color = TextPrimary, fontSize = 16.sp)
            }
        }

        Spacer(Modifier.height(18.dp))
        demoTracks.forEachIndexed { index, track ->
            TrackRow(track, index, onClick = { onPlay(index) })
        }
    }
}

@Composable
private fun PlaylistPage() {
    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
        Text("Playlists", color = TextPrimary, fontSize = 31.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(7.dp))
        Text("Small collections for the moment.", color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))

        listOf("Night Drive", "On Repeat", "Soft Hours").forEachIndexed { index, name ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlaylistTile(seed = index)
                Column(Modifier.padding(start = 14.dp)) {
                    Text(name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Text("\${index + 6} songs", color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun HeroArtwork() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(34.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF6E4B8E), Color(0xFF24172E), Color(0xFF0D0B11))
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                Color(0x45FFD7FA),
                radius = size.minDimension * .23f,
                center = androidx.compose.ui.geometry.Offset(size.width * .69f, size.height * .30f)
            )
            drawCircle(
                Color(0x251BA7FF),
                radius = size.minDimension * .38f,
                center = androidx.compose.ui.geometry.Offset(size.width * .20f, size.height * .74f)
            )
        }

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(22.dp)
        ) {
            Text(
                "AFTERGLOW",
                color = Color.White.copy(alpha = .72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.2.sp
            )
            Text(
                "Midnight Signals",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text("Oryn Radio", color = Color.White.copy(alpha = .68f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun TrackRow(track: Track, index: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) }
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumb(index)
        Column(
            Modifier
                .weight(1f)
                .padding(start = 14.dp)
        ) {
            Text(
                track.title,
                color = TextPrimary,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                track.artist,
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(Icons.Outlined.SkipNext, contentDescription = null, tint = TextMuted, modifier = Modifier.size(19.dp))
    }
}

@Composable
private fun ArtworkThumb(index: Int) {
    val gradients = listOf(
        listOf(Color(0xFFB77CFF), Color(0xFF2A1839)),
        listOf(Color(0xFF7CB7FF), Color(0xFF152A44)),
        listOf(Color(0xFFFFA37C), Color(0xFF472016)),
        listOf(Color(0xFF83E2BE), Color(0xFF173B31))
    )

    Box(
        Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(gradients[index % gradients.size]))
    )
}

@Composable
private fun PlaylistTile(seed: Int) {
    val colors = listOf(Color(0xFFC59CFF), Color(0xFF6AB7FF), Color(0xFFFFA773))

    Box(
        Modifier
            .size(58.dp)
            .clip(RoundedCornerShape(19.dp))
            .background(colors[seed % colors.size].copy(alpha = .22f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Outlined.QueueMusic,
            null,
            tint = colors[seed % colors.size],
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun MiniPlayer(
    track: Track,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xEE151318))
            .border(1.dp, Color.White.copy(alpha = .08f), RoundedCornerShape(22.dp))
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkThumb(0)
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 11.dp)
        ) {
            Text(
                track.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                track.artist,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.PlayArrow, null, tint = TextPrimary)
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Outlined.SkipNext, null, tint = TextPrimary)
        }
    }
}

@Composable
private fun LiquidGlassNavBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val slotWidth = 92.dp
    val lensWidth = 112.dp
    val lensOffset by animateDpAsState(
        targetValue = selectedIndex * slotWidth - 10.dp,
        animationSpec = spring(
            dampingRatio = .78f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "glass-lens-offset"
    )

    Box(
        modifier = modifier
            .width(292.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(38.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xDA29282E),
                        Color(0xD51B1A20),
                        Color(0xD8242329)
                    )
                )
            )
            .border(
                1.dp,
                Color.White.copy(alpha = .12f),
                RoundedCornerShape(38.dp)
            )
            .padding(4.dp)
    ) {
        // Fixed glass shell.
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = .075f),
                            Color.Transparent
                        )
                    )
                )
        )

        // The moving glass lens is intentionally wider than one slot,
        // matching the overlapping, liquid-dock behavior in the reference.
        Box(
            modifier = Modifier
                .width(lensWidth)
                .height(64.dp)
                .offset(x = lensOffset)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0x45FFFFFF),
                            Color(0x22FFFFFF),
                            Accent.copy(alpha = .08f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = .48f),
                            Color.White.copy(alpha = .12f),
                            Accent.copy(alpha = .20f)
                        )
                    ),
                    shape = RoundedCornerShape(34.dp)
                )
        ) {
            // Soft liquid highlight.
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(
                            Color.White.copy(alpha = .18f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension * .58f,
                    center = androidx.compose.ui.geometry.Offset(
                        size.width * .20f,
                        size.height * .08f
                    )
                )

                // Subtle chromatic edge accents to mimic refraction.
                drawRoundRect(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color.Transparent,
                            Color(0x55A8C7FF),
                            Color.Transparent,
                            Color(0x44D8A8FF),
                            Color.Transparent
                        )
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.3.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        34.dp.toPx(),
                        34.dp.toPx()
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val active = index == selectedIndex
                val iconTint by animateColorAsState(
                    targetValue = if (active) Accent else TextPrimary.copy(alpha = .82f),
                    animationSpec = tween(180, easing = FastOutSlowInEasing),
                    label = "dock-icon-tint"
                )
                val scale by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (active) 1.03f else 1f,
                    animationSpec = spring(
                        dampingRatio = .82f,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "dock-item-scale"
                )

                Box(
                    modifier = Modifier
                        .width(slotWidth)
                        .fillMaxSize()
                        .pointerInput(index) {
                            detectTapGestures {
                                onSelected(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            tint = iconTint,
                            modifier = Modifier
                                .size(if (active) 22.dp else 20.dp)
                                .then(
                                    Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                )
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = item.label,
                            color = if (active) Accent else TextPrimary.copy(alpha = .86f),
                            fontSize = 11.sp,
                            fontWeight = if (active) FontWeight.Medium else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
