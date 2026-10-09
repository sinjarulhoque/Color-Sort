package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DailyRecord
import com.example.viewmodel.GameViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.draw.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    viewModel: GameViewModel,
    onPlay: (String) -> Unit,
    onCalendar: () -> Unit,
    onBack: () -> Unit
) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()
    
    val today = LocalDate.now()
    val dateString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    val displayDate = today.format(DateTimeFormatter.ofPattern("MMMM d"))
    
    // We can observe the record using produceState and flow
    val dailyRecord by produceState<DailyRecord?>(initialValue = null, dateString) {
        // Technically we need to access repository, but we can do it via a function or just assume it's exposed.
        // I will add getDailyRecordFlow to ViewModel.
        viewModel.getDailyRecordFlow(dateString).collect { value = it }
    }
    
    val isCompleted = dailyRecord?.isCompleted == true

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.secondaryColor)
    ) {
        theme.backgroundAsset?.let { asset ->
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = asset),
                contentDescription = "Background",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f
            )
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("DAILY CHALLENGE", color = theme.textColor, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = theme.textColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = onCalendar) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = "History", tint = theme.textColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = theme.accentColor
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = displayDate.uppercase(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = theme.textColor
                )
                
                Text(
                    text = "Challenge #${today.dayOfYear}",
                    fontSize = 18.sp,
                    color = theme.textColor.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Reward or Completion State
                Card(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    colors = CardDefaults.cardColors(containerColor = theme.cardColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isCompleted) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("COMPLETED!", color = Color(0xFF22C55E), fontWeight = FontWeight.Bold, fontSize = 24.sp)
                            
                            Spacer(Modifier.height(16.dp))
                            Text("Best Score: ${dailyRecord?.score ?: 0}", color = theme.textColor, fontSize = 18.sp)
                            Text("Moves: ${dailyRecord?.moves ?: 0}", color = theme.textColor, fontSize = 16.sp)
                            Text("Time: ${dailyRecord?.timeSeconds ?: 0}s", color = theme.textColor, fontSize = 16.sp)
                        } else {
                            Text("Difficulty:", color = theme.textColor.copy(alpha = 0.7f))
                            Row {
                                repeat(4) {
                                    Icon(Icons.Filled.Star, null, tint = theme.accentColor, modifier = Modifier.size(24.dp))
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            Text("Reward:", color = theme.textColor.copy(alpha = 0.7f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFFFBBF24))
                                Spacer(Modifier.width(8.dp))
                                Text("100 Coins", color = theme.textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = Color(0xFF3B82F6))
                                Spacer(Modifier.width(8.dp))
                                Text("250 XP", color = theme.textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                val isMilestone = stats.dailyStreak > 0 && (stats.dailyStreak % 7 == 0 || stats.dailyStreak == 3)
                val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "streakPulse")
                
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = if (isMilestone) 1.25f else 1.05f,
                    animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                        animation = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "pulseScale"
                )

                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .scale(if (stats.dailyStreak > 0) scale else 1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥 STREAK: ", color = theme.textColor, fontWeight = FontWeight.Bold)
                    Text("${stats.dailyStreak} DAYS", color = theme.accentColor, fontWeight = FontWeight.ExtraBold)
                }

                Button(
                    onClick = { onPlay(dateString) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCompleted) theme.primaryColor else theme.accentColor)
                ) {
                    Text(if (isCompleted) "PLAY AGAIN" else "PLAY NOW", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
