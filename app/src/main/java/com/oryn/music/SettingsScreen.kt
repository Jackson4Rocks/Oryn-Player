package com.oryn.music

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LibraryMusic
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onRescan: () -> Unit,
    context: Context
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(OrynBg),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Back", tint = OrynText)
                }
                Column(Modifier.weight(1f)) {
                    Text("Settings", color = OrynText, fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
                    Text("Make ORYN feel like yours.", color = OrynMuted, fontSize = 13.sp)
                }
            }
        }

        item { SectionLabel("Library") }
        item {
            SettingRow(
                icon = Icons.Outlined.LibraryMusic,
                title = "Rescan music library",
                subtitle = "Look for new or removed songs on this device.",
                onClick = onRescan
            )
        }
        item {
            SettingRow(
                icon = Icons.Outlined.Folder,
                title = "Audio access",
                subtitle = "Manage the permission ORYN uses to read local music.",
                onClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }
            )
        }

        item { SectionLabel("Playback") }
        item {
            InfoSettingRow(
                icon = Icons.Outlined.CloudOff,
                title = "Local-first playback",
                subtitle = "ORYN plays files from your device and does not upload your music."
            )
        }
        item {
            InfoSettingRow(
                icon = Icons.Outlined.Build,
                title = "Playback controls",
                subtitle = "Tap a song to play. Open the mini-player for the full Now Playing view."
            )
        }

        item { SectionLabel("Appearance") }
        item {
            InfoSettingRow(
                icon = Icons.Outlined.Code,
                title = "AMOLED dark interface",
                subtitle = "ORYN currently uses an AMOLED-friendly dark theme by default."
            )
        }

        item { SectionLabel("About developer") }
        item {
            DeveloperCard(
                context = context
            )
        }

        item {
            Column(
                Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("ORYN Music Player", color = OrynText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Version ${BuildConfig.VERSION_NAME}", color = OrynMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        title.uppercase(),
        color = OrynAccent.copy(.82f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.7.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp, start = 4.dp)
    )
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(OrynSurface)
            .border(1.dp, Color.White.copy(.07f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxIcon(icon, OrynAccent)
        Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
            Text(title, color = OrynText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = OrynMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = OrynMuted)
    }
}

@Composable
private fun InfoSettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(OrynSurface)
            .border(1.dp, Color.White.copy(.07f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxIcon(icon, OrynMuted)
        Column(Modifier.weight(1f).padding(start = 13.dp)) {
            Text(title, color = OrynText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = OrynMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun BoxIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Box(
        Modifier.size(50.dp).clip(RoundedCornerShape(17.dp)).background(tint.copy(.10f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun DeveloperCard(context: Context) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1B1328), Color(0xFF101014))
                )
            )
            .border(1.dp, OrynAccent.copy(.14f), RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(OrynAccent, Color(0xFF3A2455))))
                    .border(2.dp, Color.White.copy(.16f), CircleShape)
            ) {
                AsyncImage(
                    model = "https://github.com/Jackson4Rocks.png?size=256",
                    contentDescription = "Leon Sony",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.size(14.dp))
            Column(Modifier.weight(1f)) {
                Text("Leon Sony", color = OrynText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("@Jackson4Rocks", color = OrynAccent, fontSize = 12.sp)
                Text("Android • Linux • Low-level developer", color = OrynMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "ORYN is built as a local-first music player with a clean, expressive Android interface.",
            color = OrynMuted,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DevButton("GitHub") { open(context, "https://github.com/Jackson4Rocks") }
            DevButton("Website") { open(context, "https://jackson4rocks.github.io") }
            DevButton("ORYN") { open(context, "https://github.com/Jackson4Rocks/Oryn-Player") }
        }
    }
}

@Composable
private fun DevButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(.06f),
            contentColor = OrynText
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp)
    }
}

private fun open(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}