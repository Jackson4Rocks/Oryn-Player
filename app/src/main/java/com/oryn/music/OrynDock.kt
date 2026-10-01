package com.oryn.music

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val dockItems = listOf(
    "Home" to Icons.Outlined.Home,
    "Library" to Icons.Outlined.LibraryMusic,
    "Playlists" to Icons.Outlined.QueueMusic
)

@Composable
fun LiquidGlassDock(
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.navigationBars),
        contentAlignment = Alignment.BottomCenter
    ) {
        BoxWithConstraints(
            Modifier
                .fillMaxWidth(.90f)
                .widthIn(max = 348.dp)
                .height(74.dp)
                .border(1.dp, Color.White.copy(.13f), RoundedCornerShape(38.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xE52A2930),
                            Color(0xDD17161C),
                            Color(0xE3212026)
                        )
                    ),
                    RoundedCornerShape(38.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            val itemWidth = maxWidth / 3f
            val lensWidth = itemWidth * 1.20f
            val targetOffset = itemWidth * selectedIndex - (lensWidth - itemWidth) / 2f
            val lensOffset by animateDpAsState(
                targetValue = targetOffset,
                animationSpec = spring(
                    dampingRatio = .76f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "liquid-lens"
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(Color.White.copy(.08f), Color.Transparent)
                        ),
                        RoundedCornerShape(34.dp)
                    )
            )

            Box(
                Modifier
                    .width(lensWidth)
                    .height(64.dp)
                    .offset(x = lensOffset)
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(.46f),
                                Color.White.copy(.12f),
                                OrynAccent.copy(.20f)
                            )
                        ),
                        RoundedCornerShape(34.dp)
                    )
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0x4DFFFFFF),
                                Color(0x20FFFFFF),
                                OrynAccent.copy(.10f)
                            )
                        ),
                        RoundedCornerShape(34.dp)
                    )
            ) {
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color.White.copy(.18f), Color.Transparent)
                        ),
                        radius = size.minDimension * .60f,
                        center = androidx.compose.ui.geometry.Offset(
                            size.width * .18f,
                            size.height * .06f
                        )
                    )
                }
            }

            Row(Modifier.fillMaxSize()) {
                dockItems.forEachIndexed { index, item ->
                    val active = index == selectedIndex
                    val tint by animateColorAsState(
                        targetValue = if (active) OrynAccent else OrynText.copy(.84f),
                        animationSpec = tween(180, easing = FastOutSlowInEasing),
                        label = "dock-tint"
                    )
                    Column(
                        Modifier
                            .width(itemWidth)
                            .fillMaxSize()
                            .clickable { onSelected(index) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            item.second,
                            contentDescription = item.first,
                            tint = tint,
                            modifier = Modifier.size(if (active) 22.dp else 20.dp)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            item.first,
                            color = if (active) OrynAccent else OrynText.copy(.82f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}