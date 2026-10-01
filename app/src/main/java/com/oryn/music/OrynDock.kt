package com.oryn.music

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val items = listOf(
    "Home" to Icons.Outlined.Home,
    "Library" to Icons.Outlined.LibraryMusic,
    "Playlists" to Icons.Outlined.QueueMusic
)

@Composable
fun OrynBottomBar(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, bottom = 10.dp)
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .height(64.dp)
                .weight(1f, fill = false)
                .background(OrynSurface2, RoundedCornerShape(32.dp))
                .border(1.dp, Color.White.copy(.08f), RoundedCornerShape(32.dp))
                .padding(horizontal = 5.dp)
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val active = index == selectedIndex
                val tint by animateColorAsState(
                    if (active) OrynText else OrynMuted,
                    animationSpec = tween(180),
                    label = "photos-style-tint"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .background(
                            if (active) OrynAccent.copy(alpha = .16f) else Color.Transparent,
                            RoundedCornerShape(27.dp)
                        )
                        .clickable { onSelected(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        item.second,
                        item.first,
                        tint = tint,
                        modifier = Modifier.size(if (active) 20.dp else 19.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        item.first,
                        color = tint,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(OrynSurface2, CircleShape)
                .border(1.dp, Color.White.copy(.08f), CircleShape)
                .clickable(onClick = onSearch),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Search music",
                tint = OrynText,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}