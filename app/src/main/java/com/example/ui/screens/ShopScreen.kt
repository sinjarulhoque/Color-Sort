package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.ThemeRepository
import com.example.models.UnlockType
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(viewModel: GameViewModel, onBackClicked: () -> Unit) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("LIVES") }
    val categories = listOf("LIVES", "BOOSTS", "COINS", "THEMES")

    fun toast(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop", color = Color.White, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Stars, contentDescription = "Coins", tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${stats.coins}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))))
            )

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            title = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedCategory) {
                        "LIVES" -> {
                            item { ShopHeader("Lives & Health", "Stay in the game") }
                            item {
                                ShopItemCard(
                                    title = "+1 Life", description = "Get 1 extra life immediately",
                                    icon = Icons.Filled.Favorite, iconTint = Color(0xFFEF4444),
                                    cost = 100, canAfford = stats.coins >= 100,
                                    onBuy = {
                                        if (stats.coins >= 100) { viewModel.buyLives(1, 100); toast("+1 Life purchased") }
                                        else toast("Not enough coins")
                                    }
                                )
                            }
                            item {
                                ShopItemCard(
                                    title = "Full Refill (+5)", description = "Completely restore all your lives",
                                    icon = Icons.Filled.FavoriteBorder, iconTint = Color(0xFFF43F5E),
                                    cost = 400, canAfford = stats.coins >= 400, badge = "SAVE 20%",
                                    onBuy = {
                                        if (stats.coins >= 400) { viewModel.buyLives(5, 400); toast("Lives refilled") }
                                        else toast("Not enough coins")
                                    }
                                )
                            }
                        }
                        "BOOSTS" -> {
                            item { ShopHeader("Power-ups", "Tools to solve difficult puzzles") }
                            item {
                                ShopItemCard(
                                    title = "5 Hints", description = "Shows the next optimal move",
                                    icon = Icons.Filled.Lightbulb, iconTint = Color(0xFFFACC15),
                                    cost = 250, canAfford = stats.coins >= 250,
                                    onBuy = {
                                        if (viewModel.buyHints(5, 250)) toast("+5 Hints") else toast("Not enough coins")
                                    }
                                )
                            }
                            item {
                                ShopItemCard(
                                    title = "5 Undos", description = "Reverse your last mistake",
                                    icon = Icons.Filled.Undo, iconTint = Color(0xFF60A5FA),
                                    cost = 200, canAfford = stats.coins >= 200,
                                    onBuy = {
                                        if (viewModel.buyUndos(5, 200)) toast("+5 Undos") else toast("Not enough coins")
                                    }
                                )
                            }
                            item {
                                ShopItemCard(
                                    title = "Extra Tube", description = "Add an empty tube to any level",
                                    icon = Icons.Filled.Science, iconTint = Color(0xFF4ADE80),
                                    cost = 500, canAfford = stats.coins >= 500, badge = "POWERFUL",
                                    onBuy = {
                                        if (viewModel.buyExtraTubes(1, 500)) toast("+1 Extra Tube") else toast("Not enough coins")
                                    }
                                )
                            }
                            item {
                                Text(
                                    "You own ${stats.extraTubes} extra tube(s). Use them mid-level with the tube button.",
                                    color = Color.Gray, fontSize = 12.sp
                                )
                            }
                        }
                        "COINS" -> {
                            item { ShopHeader("Coin Packs", "Get more coins for power-ups") }
                            item {
                                val claimedToday = viewModel.hasClaimedDailyFreeCoins()
                                ShopItemCard(
                                    title = "Daily Free Coins", description = if (claimedToday) "Come back tomorrow!" else "Claim your daily bonus!",
                                    icon = Icons.Filled.Stars, iconTint = Color(0xFFF59E0B),
                                    cost = 0, canAfford = !claimedToday,
                                    onBuy = {
                                        if (viewModel.claimDailyFreeCoins(250)) toast("+250 Coins") else toast("Already claimed today")
                                    }
                                )
                            }
                            // Real-money packs require Google Play Billing (not available in this build).
                            item {
                                ShopItemCard(
                                    title = "Handful of Coins", description = "1,000 Coins",
                                    icon = Icons.Filled.MonetizationOn, iconTint = Color(0xFFFCD34D),
                                    cost = -1, canAfford = false, priceLabel = "$0.99",
                                    onBuy = { toast("In-app purchase coming soon") }
                                )
                            }
                            item {
                                ShopItemCard(
                                    title = "Bag of Coins", description = "5,000 Coins",
                                    icon = Icons.Filled.LocalMall, iconTint = Color(0xFFF59E0B),
                                    cost = -1, canAfford = false, priceLabel = "$3.99", badge = "POPULAR",
                                    onBuy = { toast("In-app purchase coming soon") }
                                )
                            }
                        }
                        "THEMES" -> {
                            item { ShopHeader("Visual Themes", "Customize your puzzle experience") }
                            items(ThemeRepository.themes) { theme ->
                                val unlocked = stats.unlockedThemes.split(",").contains(theme.id)
                                val selected = stats.selectedThemeId == theme.id
                                val canAfford = stats.coins >= theme.unlockValue
                                val label = when {
                                    selected -> "SELECTED"
                                    unlocked -> "SELECT"
                                    theme.unlockType == UnlockType.LEVEL -> "LVL ${theme.unlockValue}"
                                    else -> "${theme.unlockValue}"
                                }
                                ThemeCard(
                                    theme = theme,
                                    unlocked = unlocked,
                                    selected = selected,
                                    label = label,
                                    canAfford = canAfford,
                                    onAction = {
                                        when {
                                            selected -> toast("Already selected")
                                            unlocked -> { viewModel.selectTheme(theme.id); toast("${theme.name} selected") }
                                            theme.unlockType == UnlockType.LEVEL -> toast("Reach level ${theme.unlockValue}")
                                            canAfford -> {
                                                if (viewModel.unlockTheme(theme)) toast("${theme.name} unlocked!") else toast("Not enough coins")
                                            }
                                            else -> toast("Not enough coins")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B)
    val textColor = if (isSelected) Color.White else Color.Gray
    val borderColor = if (isSelected) Color(0xFF60A5FA) else Color.Transparent

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ShopHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Text(subtitle, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun ShopItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    cost: Int,
    canAfford: Boolean,
    onBuy: () -> Unit,
    badge: String? = null,
    priceLabel: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.background(Color(0xFFE11D48), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = Color.Gray, fontSize = 13.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford || cost == 0 || priceLabel != null,
                modifier = Modifier.width(90.dp).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (cost == 0) Color(0xFF10B981) else if (priceLabel != null) Color(0xFF8B5CF6) else Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF334155)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (priceLabel != null) {
                    Text(priceLabel, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                } else if (cost == 0) {
                    Text("FREE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text("$cost", color = if (canAfford) Color.White else Color.Gray, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = if (canAfford) Color(0xFFFCD34D) else Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: com.example.models.GameTheme,
    unlocked: Boolean,
    selected: Boolean,
    label: String,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    val isLocked = !unlocked && theme.unlockType == com.example.models.UnlockType.LEVEL
    Card(
        modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.horizontalGradient(listOf(theme.primaryColor, theme.accentColor)),
                        RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Palette, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(theme.name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    when (theme.unlockType) {
                        com.example.models.UnlockType.FREE -> "Free theme"
                        com.example.models.UnlockType.COINS -> "Costs ${theme.unlockValue} coins"
                        com.example.models.UnlockType.LEVEL -> "Unlocks at level ${theme.unlockValue}"
                    },
                    color = Color.Gray, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = onAction,
                enabled = !selected && (!isLocked) && (unlocked || canAfford),
                modifier = Modifier.width(110.dp).height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected) Color(0xFF10B981) else Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF334155)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    label,
                    color = if (selected) Color.White else Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
