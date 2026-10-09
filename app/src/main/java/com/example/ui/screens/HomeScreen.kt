package com.example.ui.screens
 
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.ui.components.ProfileAvatar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseUser
import com.example.ui.components.AdBanner
import com.example.ui.theme.AppPrefs
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.MinimalBackground
import com.example.ui.theme.rememberCountdown

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onPlayClicked: (Int) -> Unit,
    onThemesClicked: () -> Unit,
    onDailyClicked: () -> Unit,
    onShopClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onAchievementsClicked: () -> Unit,
    onDailyLoginClicked: () -> Unit,
    onStreakClicked: () -> Unit,
    onEventsClicked: () -> Unit,
    onSettingsClicked: () -> Unit = {},
    onBottomNavClicked: (String) -> Unit,
    user: FirebaseUser? = null,
    onFreeCoinsClicked: () -> Unit = onShopClicked
) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val dailyTarget = AppPrefs.dailyRewardTarget(context)
    val freeTarget = AppPrefs.freeCoinsTarget(context)
    val dailyCountdown = rememberCountdown(dailyTarget)
    val freeCountdown = rememberCountdown(freeTarget)
    val dailyTime = if (dailyTarget == 0L) "READY" else dailyCountdown
    val freeTime = if (freeTarget == 0L) "READY" else freeCountdown
    var homeVisible by remember { mutableStateOf(false) }
    val homeAlpha by animateFloatAsState(if (homeVisible) 1f else 0f, label = "home_alpha")
    val homeScale by animateFloatAsState(if (homeVisible) 1f else 0.96f, label = "home_scale")
    LaunchedEffect(Unit) { homeVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        MinimalBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                Column(modifier = Modifier.background(colors.surface)) {
                    AdBanner(modifier = Modifier.fillMaxWidth())
                    BottomGameNavigation(onNavClicked = onBottomNavClicked)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .graphicsLayer { alpha = homeAlpha; scaleX = homeScale; scaleY = homeScale },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameHeader(
                    level = stats.highestUnlockedLevel,
                    xp = stats.xp,
                    coins = stats.coins,
                    lives = stats.lives,
                    onProfileClicked = onProfileClicked,
                    onSettingsClicked = onSettingsClicked,
                    onAddCoinsClicked = onShopClicked,
                    onAddLivesClicked = onShopClicked,
                    user = user,
                    avatarIndex = stats.avatarIndex
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GameLogo()
                    }
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 12.dp, top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SideActionButton(icon = Icons.Filled.CardGiftcard, title = "DAILY REWARD", time = dailyTime, bgColor = Color(0xFF4C1D95).copy(alpha=0.9f), onClick = onDailyLoginClicked)
                        SideActionButton(icon = Icons.Filled.Lock, title = "FREE COINS", time = freeTime, bgColor = Color(0xFF4C1D95).copy(alpha=0.9f), onClick = onFreeCoinsClicked)
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                PrimaryPlayButton(onClick = { onPlayClicked(stats.highestUnlockedLevel) })
                
                Spacer(modifier = Modifier.height(16.dp))
                DailyChallengeButton(onClick = onDailyClicked)
                
                Spacer(modifier = Modifier.height(18.dp))
                FeatureGrid(
                    onThemesClicked = onThemesClicked,
                    onShopClicked = onShopClicked,
                    onLeaderboardClicked = onLeaderboardClicked,
                    onProfileClicked = onProfileClicked,
                    onAchievementsClicked = onAchievementsClicked,
                    onDailyLoginClicked = onDailyLoginClicked,
                    onStreakClicked = onStreakClicked,
                    onEventsClicked = onEventsClicked
                )
                
                Spacer(modifier = Modifier.height(20.dp)) 
            }
        }
    }
}

@Composable
fun GameHeader(
    level: Int, 
    xp: Int, 
    coins: Int, 
    lives: Int, 
    onProfileClicked: () -> Unit,
    onAddCoinsClicked: () -> Unit,
    onAddLivesClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    user: FirebaseUser? = null,
    avatarIndex: Int = -1
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val compact = maxWidth < 400.dp
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            ProfileSummary(level, xp, onProfileClicked, user, Modifier.weight(1f), compact, avatarIndex)
            ResourceSummary(coins, lives, onAddCoinsClicked, onAddLivesClicked, onSettingsClicked, compact)
        }
    }
}

@Composable
private fun ProfileSummary(level: Int, xp: Int, onProfileClicked: () -> Unit, user: FirebaseUser?, modifier: Modifier = Modifier, compact: Boolean = false, avatarIndex: Int = -1) {
    val colors = LocalAppColors.current
    val name = user?.displayName?.takeIf { it.isNotBlank() } ?: user?.email?.substringBefore("@") ?: "Player"
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.clickable { onProfileClicked() }) {
            Box(
                modifier = Modifier
                    .size(if (compact) 48.dp else 56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B))
                    .border(2.dp, Color(0xFFFCD34D), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ProfileAvatar(seed = name, index = avatarIndex, size = (if (compact) 48.dp else 56.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(if (compact) 18.dp else 22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFACC15))
                        .border(1.dp, Color.Black.copy(alpha=0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$level", color = Color.Black, fontSize = if (compact) 8.sp else 10.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.width(if (compact) 6.dp else 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(name, color = colors.primaryText, fontWeight = FontWeight.ExtraBold, fontSize = if (compact) 14.sp else 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF2563EB)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(10.dp))
                    }
                }
                Text("Level $level", color = Color(0xFF60A5FA), fontWeight = FontWeight.Bold, fontSize = if (compact) 11.sp else 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.width(if (compact) 72.dp else 110.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(colors.border)) {
                    val progress = if (level > 0) (xp % 1000) / 1000f else 0f
                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progress.coerceAtLeast(0.01f)).background(Color(0xFF3B82F6)))
                }
                val targetXp = (level * 1000).coerceAtLeast(1000)
                Text("$xp / $targetXp XP", color = colors.secondaryText, fontSize = if (compact) 7.sp else 9.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }

@Composable
private fun ResourceSummary(coins: Int, lives: Int, onAddCoinsClicked: () -> Unit, onAddLivesClicked: () -> Unit, onSettingsClicked: () -> Unit, compact: Boolean = false) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ResourceCounter(
                icon = {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                        Text("$lives", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = "", 
                suffix = if (compact) "" else if (lives >= 5) "FULL" else "", 
                onClick = onAddLivesClicked
            )
            Spacer(modifier = Modifier.width(if (compact) 3.dp else 8.dp))
            ResourceCounter(
                icon = {
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFF59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(12.dp))
                    }
                },
                text = "$coins",
                onClick = onAddCoinsClicked
            )
                Spacer(modifier = Modifier.width(if (compact) 3.dp else 8.dp))
            
            // Settings button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E3A8A))
                    .clickable(onClick = onSettingsClicked),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

@Composable
fun ResourceCounter(icon: @Composable () -> Unit, text: String, suffix: String = "", onClick: () -> Unit = {}) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .background(colors.card, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        if (suffix.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(suffix, color = colors.primaryText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(Color(0xFF22C55E)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
fun SideActionButton(icon: ImageVector, title: String, time: String, bgColor: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(78.dp)
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = title, tint = Color(0xFFF472B6), modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(time, color = Color(0xFFFDE047), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GameLogo() {
    val colors = LocalAppColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            Text("C", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF38BDF8), modifier = Modifier.shadow(8.dp))
            Text("O", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFFACC15), modifier = Modifier.shadow(8.dp))
            Text("L", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEC4899), modifier = Modifier.shadow(8.dp))
            Text("O", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF4ADE80), modifier = Modifier.shadow(8.dp))
            Text("R", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444), modifier = Modifier.shadow(8.dp))
        }
        Text("SORT", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = colors.primaryText, modifier = Modifier.offset(y = (-24).dp).shadow(8.dp))
        
        Box(
            modifier = Modifier
                .offset(y = (-36).dp)
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF7E22CE), Color(0xFF4C1D95))),
                    shape = RoundedCornerShape(24.dp)
                )
                .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(24.dp))
                .padding(horizontal = 24.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.color_sort_icon_1787713756274),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    contentScale = ContentScale.Crop
                )
                Text("PUZZLE", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 2.sp)
            }
        }
    }
}

@Composable
fun PrimaryPlayButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(84.dp)
            .shadow(16.dp, RoundedCornerShape(42.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(listOf(Color(0xFFFCD34D), Color(0xFFF59E0B), Color(0xFFD97706))),
                    shape = RoundedCornerShape(42.dp)
                )
                .border(2.dp, Color(0xFFFEF3C7), RoundedCornerShape(42.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("PLAY", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF451A03))
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color(0xFF451A03), modifier = Modifier.size(42.dp))
            }
        }
    }
}

@Composable
fun DailyChallengeButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(68.dp)
            .shadow(8.dp, RoundedCornerShape(34.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFF9333EA).copy(alpha=0.9f), Color(0xFF6B21A8).copy(alpha=0.9f))),
                    shape = RoundedCornerShape(34.dp)
                )
                .border(2.dp, Color(0xFFD8B4FE), RoundedCornerShape(34.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = "Trophy", tint = Color(0xFFFBBF24), modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("DAILY CHALLENGE", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("New Challenge Available!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE047))
                }
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Go", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
fun FeatureGrid(
    onThemesClicked: () -> Unit,
    onShopClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onProfileClicked: () -> Unit,
    onAchievementsClicked: () -> Unit,
    onDailyLoginClicked: () -> Unit,
    onStreakClicked: () -> Unit,
    onEventsClicked: () -> Unit
) {
    val items = listOf(
        Triple("LEADERBOARD", "Top Players", Color(0xFF2563EB) to Color(0xFF1E3A8A)),
        Triple("PROFILE", "Your Stats", Color(0xFF0D9488) to Color(0xFF115E59)),
        Triple("ACHIEVEMENTS", "23 / 68", Color(0xFFD97706) to Color(0xFF92400E)),
        Triple("THEMES", "Customize", Color(0xFF9333EA) to Color(0xFF581C87)),
        Triple("SHOP", "Coins & Items", Color(0xFFE11D48) to Color(0xFF9F1239)),
        Triple("DAILY LOGIN", "Day 4", Color(0xFF2563EB) to Color(0xFF1E40AF)),
        Triple("STREAK", "7 Days", Color(0xFF16A34A) to Color(0xFF14532D)),
        Triple("EVENTS", "Special Events", Color(0xFF4F46E5) to Color(0xFF312E81))
    )
    val icons = listOf(
        Icons.Filled.EmojiEvents, Icons.Filled.Person, Icons.Filled.MilitaryTech, Icons.Filled.Palette,
        Icons.Filled.ShoppingCart, Icons.Filled.CalendarMonth, Icons.Filled.LocalFireDepartment, Icons.Filled.AutoAwesome
    )
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.height(260.dp).fillMaxWidth() 
    ) {
        items(items.size) { index ->
            FeatureCard(
                title = items[index].first,
                subtitle = items[index].second,
                icon = icons[index],
                colors = items[index].third,
                onClick = {
                    when (items[index].first) {
                        "THEMES" -> onThemesClicked()
                        "SHOP" -> onShopClicked()
                        "LEADERBOARD" -> onLeaderboardClicked()
                        "PROFILE" -> onProfileClicked()
                        "ACHIEVEMENTS" -> onAchievementsClicked()
                        "DAILY LOGIN" -> onDailyLoginClicked()
                        "STREAK" -> onStreakClicked()
                        "EVENTS" -> onEventsClicked()
                    }
                }
            )
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, icon: ImageVector, colors: Pair<Color, Color>, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(colors.first.copy(alpha=0.9f), colors.second.copy(alpha=0.9f))))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(title, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 8.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(6.dp))
                Box(modifier = Modifier.size(18.dp).background(Color.Black.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun BottomGameNavigation(onNavClicked: (String) -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(icon = Icons.Filled.Home, label = "HOME", selected = true, onClick = { onNavClicked("home") })
            NavItem(icon = Icons.Filled.Style, label = "LEVELS", selected = false, onClick = { onNavClicked("levels") }) // Style icon somewhat resembles layered maps/shirts
            
            Box(
                modifier = Modifier
                    .offset(y = (-24).dp)
                    .size(76.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFFD946EF), Color(0xFF9333EA), Color(0xFF4C1D95))), CircleShape)
                    .border(2.dp, Color(0xFFC084FC).copy(alpha=0.6f), CircleShape)
                    .shadow(12.dp, CircleShape)
                    .clickable { onNavClicked("game") },
                contentAlignment = Alignment.Center
            ) {
                // Tube icon graphic
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TubeGraphic(Color(0xFFEF4444))
                    TubeGraphic(Color(0xFFFACC15))
                    TubeGraphic(Color(0xFF38BDF8))
                }
            }
            
            NavItem(icon = Icons.Filled.People, label = "FRIENDS", selected = false, onClick = { onNavClicked("friends") })
            NavItem(icon = Icons.Filled.Mail, label = "INBOX", selected = false, showDot = true, onClick = { onNavClicked("inbox") })
        }
    }
}

@Composable
fun TubeGraphic(color: Color) {
    Box(
        modifier = Modifier
            .width(12.dp)
            .height(34.dp)
            .border(1.dp, Color.White.copy(alpha=0.7f), RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
            .clip(RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha=0.2f)))
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).align(Alignment.BottomCenter).background(color))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.TopCenter).background(Color.White.copy(alpha=0.5f)))
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, selected: Boolean, showDot: Boolean = false, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(64.dp)
            .then(
                if (selected) Modifier.background(Color(0xFF1E3A8A).copy(alpha=0.5f), RoundedCornerShape(32.dp)).border(1.dp, Color(0xFF3B82F6).copy(alpha=0.5f), RoundedCornerShape(32.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Icon(icon, contentDescription = label, tint = if (selected) Color(0xFFFCD34D) else Color.Gray, modifier = Modifier.size(22.dp))
                if (showDot) {
                    Box(modifier = Modifier.size(10.dp).align(Alignment.TopEnd).offset(x=2.dp, y=(-2).dp).background(Color(0xFFEF4444), CircleShape).border(1.dp, Color(0xFF13113C), CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = if (selected) Color(0xFFFCD34D) else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
