package com.oryn.music

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrynHeader(
    searchOpen: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchToggle: () -> Unit,
    onAbout: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (searchOpen) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .background(OrynSurface, RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(.08f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp),
                singleLine = true,
                textStyle = TextStyle(color = OrynText, fontSize = 15.sp),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isBlank()) Text("Search your music…", color = OrynMuted, fontSize = 15.sp)
                        inner()
                    }
                }
            )
            IconButton(onClick = onSearchToggle) {
                Icon(Icons.Outlined.Close, "Close search", tint = OrynText)
            }
        } else {
            Column(Modifier.weight(1f)) {
                Text("ORYN", color = OrynText, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Text("LOCAL • PRIVATE • YOUR MUSIC", color = OrynMuted, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.35.sp)
            }
            IconButton(onClick = onSearchToggle) {
                Icon(Icons.Outlined.Search, "Search", tint = OrynText)
            }
            IconButton(onClick = onAbout) {
                Icon(Icons.Outlined.Person, "About", tint = OrynAccent)
            }
        }
    }
}