package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

private val AVATAR_EMOJIS = listOf(
    "\uD83E\uDD8A", "\uD83D\uDC3C", "\uD83D\uDC28", "\uD83E\uDD81",
    "\uD83D\uDC38", "\uD83D\uDC35", "\uD83D\uDC2F", "\uD83D\uDC31",
    "\uD83D\uDC36", "\uD83D\uDC30", "\uD83E\uDD84", "\uD83D\uDC22"
)

private val AVATAR_COLORS = listOf(
    0xFFEF4444, 0xFFF59E0B, 0xFF10B981, 0xFF3B82F6,
    0xFF8B5CF6, 0xFFEC4899, 0xFF14B8A6, 0xFFF97316,
    0xFF6366F1, 0xFF22C55E, 0xFFEAB308, 0xFF06B6D4
)

const val AVATAR_COUNT = 12

fun avatarIndexFromSeed(seed: String, storedIndex: Int): Int {
    val base = if (storedIndex >= 0) storedIndex else if (seed.isBlank()) 0 else seed.hashCode()
    val positive = if (base < 0) -base else base
    return positive % AVATAR_COUNT
}

@Composable
fun ProfileAvatar(seed: String, index: Int, size: Dp, modifier: Modifier = Modifier) {
    val idx = avatarIndexFromSeed(seed, index)
    val color = Color(AVATAR_COLORS[idx % AVATAR_COLORS.size])
    val emoji = AVATAR_EMOJIS[idx % AVATAR_EMOJIS.size]
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = (size.value * 0.55f).sp, fontWeight = FontWeight.Bold)
    }
}
