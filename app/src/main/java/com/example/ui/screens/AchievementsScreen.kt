package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GameViewModel

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val current: Int,
    val total: Int,
    val reward: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(viewModel: GameViewModel, onBackClicked: () -> Unit) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val levelProgress by viewModel.allLevelProgress.collectAsStateWithLifecycle()
    val dailyRecords by viewModel.allDailyRecords.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val completedLevels = levelProgress.count { it.isCompleted }
    val dailyCompleted = dailyRecords.count { it.isCompleted }
    val unlockedThemes = stats.unlockedThemes.split(",").count { it.isNotBlank() }

    val achievements = listOf(
        Achievement("first_win", "First Win", "Complete your first level", completedLevels, 1, 200),
        Achievement("level_10", "Level Explorer", "Complete 10 levels", completedLevels, 10, 500),
        Achievement("level_50", "Level Master", "Complete 50 levels", completedLevels, 50, 2000),
        Achievement("speed_demon", "Speed Demon", "Finish a level in under 30s", stats.speedSolves, 1, 300),
        Achievement("daily_10", "Daily Devotee", "Complete 10 Daily Challenges", dailyCompleted, 10, 1000),
        Achievement("streak_7", "Week Warrior", "Reach a 7-day streak", stats.dailyStreak, 7, 500),
        Achievement("streak_30", "Streak Legend", "Reach a 30-day streak", stats.dailyStreak, 30, 3000),
        Achievement("collector", "Theme Collector", "Unlock 3 themes", unlockedThemes, 3, 800)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Achievements", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(achievements.size) { i ->
                val a = achievements[i]
                val isCompleted = a.current >= a.total
                val isClaimed = viewModel.isAchievementClaimed(a.id)
                val canClaim = isCompleted && !isClaimed

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).background(if (isCompleted) Color(0xFFF59E0B) else Color.DarkGray, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(a.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(a.description, color = Color.Gray, fontSize = 12.sp)
                            }
                            when {
                                isClaimed -> Text("CLAIMED", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                canClaim -> Button(
                                    onClick = {
                                        if (viewModel.claimAchievement(a.id, a.reward)) Toast.makeText(context, "+${a.reward} coins", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("CLAIM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                else -> Text("${minOf(a.current, a.total)} / ${a.total}", color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (!isCompleted) {
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = minOf(a.current, a.total).toFloat() / a.total.toFloat(),
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF38BDF8),
                                trackColor = Color.Black.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}
