package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFireDepartment
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

data class StreakMilestone(val days: Int, val reward: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreakScreen(viewModel: GameViewModel, onBackClicked: () -> Unit) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val streak = stats.dailyStreak
    val longest = maxOf(stats.longestStreak, streak)
    val milestones = listOf(
        StreakMilestone(3, 150),
        StreakMilestone(7, 400),
        StreakMilestone(14, 1000),
        StreakMilestone(30, 3000)
    )
    val nextMilestone = milestones.firstOrNull { it.days > streak } ?: milestones.last()
    val prevMilestoneDays = milestones.lastOrNull { it.days <= streak }?.days ?: 0
    val progress = if (nextMilestone.days > prevMilestoneDays)
        (streak - prevMilestoneDays).toFloat() / (nextMilestone.days - prevMilestoneDays).toFloat()
    else 1f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Streak", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("CURRENT STREAK", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("$streak DAYS", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("LONGEST STREAK: $longest DAYS", color = Color(0xFFFACC15), fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Progress to ${nextMilestone.days} Days", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Reward: +${nextMilestone.reward} Coins", color = Color(0xFFFACC15), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color.Black.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$streak / ${nextMilestone.days} Days", color = Color.White, fontSize = 12.sp, modifier = Modifier.align(Alignment.End))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Milestones", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(milestones.size) { i ->
                    val m = milestones[i]
                    val reached = streak >= m.days
                    val claimed = viewModel.isStreakMilestoneClaimed(m.days)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF172033)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp).background(if (reached) Color(0xFFF97316) else Color.DarkGray, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${m.days} Day Streak", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("+${m.reward} Coins", color = Color(0xFFFACC15), fontSize = 12.sp)
                            }
                            when {
                                claimed -> Text("CLAIMED", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                reached -> Button(
                                    onClick = {
                                        if (viewModel.claimStreakMilestone(m.days, m.reward)) Toast.makeText(context, "+${m.reward} coins", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text("CLAIM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                else -> Text("Locked", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Play a Daily Challenge each day to keep your streak alive.",
                color = Color.Gray, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}
