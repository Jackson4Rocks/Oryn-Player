package com.oryn.music

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun MiniPlayer(
    track: Track,
    playing: Boolean,
    onOpen: () -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(bottom = 92.dp)
            .height(70.dp)
            .clickable(onClick = onOpen)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xF21A181E))
            .border(1.dp, Color.White.copy(.11f), RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtworkTile(track.id)
        Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
            Text(track.title, color = OrynText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = OrynMuted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        IconButton(onClick = onPrevious) { Icon(Icons.Outlined.SkipPrevious, "Previous", tint = OrynText) }
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(46.dp).clip(CircleShape).background(OrynAccent.copy(.14f))
        ) {
            Icon(if (playing) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, "Play or pause", tint = OrynAccent)
        }
        IconButton(onClick = onNext) { Icon(Icons.Outlined.SkipNext, "Next", tint = OrynText) }
    }
}

@Composable
fun AboutOverlay(onDismiss: () -> Unit, context: Context) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(.68f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(OrynSurface2)
                .border(1.dp, Color.White.copy(.10f), RoundedCornerShape(32.dp))
                .pointerInput(Unit) { detectTapGestures { } }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(OrynAccent, Color(0xFF3A2455))))
                    .border(2.dp, Color.White.copy(.18f), CircleShape)
            ) {
                AsyncImage(
                    model = "https://github.com/Jackson4Rocks.png?size=256",
                    contentDescription = "Leon Sony",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Leon Sony", color = OrynText, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text("@Jackson4Rocks", color = OrynAccent, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("Android • Linux • Low-level developer", color = OrynMuted, fontSize = 13.sp)
            Text(
                "Building ORYN around local-first music and expressive interfaces.",
                color = OrynMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 6.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AboutButton("GitHub") { openUrl(context, "https://github.com/Jackson4Rocks") }
                AboutButton("Website") { openUrl(context, "https://jackson4rocks.github.io") }
                AboutButton("ORYN") { openUrl(context, "https://github.com/Jackson4Rocks/Oryn-Player") }
            }
            Spacer(Modifier.height(14.dp))
            Text("ORYN 0.1.0 • local-first music", color = OrynMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AboutButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(.06f), contentColor = OrynText),
        shape = RoundedCornerShape(17.dp)
    ) {
        Text(label)
    }
}

@Composable
fun PermissionScreen(onRequest: () -> Unit, onOpenSettings: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(OrynBg),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Outlined.LibraryMusic, null, tint = OrynAccent, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(18.dp))
            Text("ORYN needs your music", color = OrynText, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Allow audio access so ORYN can find the songs already stored on this device. Nothing needs to be uploaded.",
                color = OrynMuted,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onRequest, shape = RoundedCornerShape(18.dp)) { Text("Allow music access") }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(.06f), contentColor = OrynText),
                shape = RoundedCornerShape(18.dp)
            ) { Text("Open app settings") }
        }
    }
}

@Composable
fun EmptyLibraryCard() {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(OrynSurface).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(26.dp)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Outlined.Album, null, tint = OrynAccent, modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(10.dp))
        Text("No music found", color = OrynText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text("Add audio files to your device and reopen or rescan ORYN.", color = OrynMuted, fontSize = 13.sp)
    }
}

@Composable
fun LoadingCard() {
    Box(
        Modifier.fillMaxWidth().height(84.dp).clip(RoundedCornerShape(24.dp)).background(OrynSurface).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text("Scanning your music…", color = OrynMuted, fontSize = 13.sp)
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(22.dp)).background(OrynSurface).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(22.dp)).padding(14.dp)) {
        Text(value, color = OrynText, fontSize = 21.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = OrynMuted, fontSize = 11.sp)
    }
}

@Composable
fun InfoPill(label: String, icon: ImageVector, modifier: Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(18.dp)).background(OrynSurface).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(18.dp)).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(icon, null, tint = OrynAccent, modifier = Modifier.size(17.dp))
        Text(label, color = OrynText, fontSize = 12.sp)
    }
}

@Composable
fun GlassSectionHeader(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
        Text(title, color = OrynText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = OrynMuted, fontSize = 11.sp)
    }
}

@Composable
fun PlaylistCard(title: String, subtitle: String, icon: ImageVector, accent: Color) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(OrynSurface).border(1.dp, Color.White.copy(.07f), RoundedCornerShape(24.dp)).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(17.dp)).background(accent.copy(.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = accent)
        }
        Column(Modifier.padding(start = 13.dp)) {
            Text(title, color = OrynText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = OrynMuted, fontSize = 12.sp)
        }
    }
}

fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}

private fun openUrl(context: Context, value: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(value)))
}