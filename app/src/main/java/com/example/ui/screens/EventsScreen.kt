package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GameEvent
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(viewModel: GameViewModel, onBackClicked: () -> Unit) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val events = remember(stats) { viewModel.getEvents() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events", color = Color.White, fontWeight = FontWeight.Bold) },
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
            item {
                Box(modifier = Modifier.fillMaxWidth().height(90.dp).clip(RoundedCornerShape(16.dp)).background(
                    Color(0xFF8B5CF6)
                ), contentAlignment = Alignment.CenterStart) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("LIVE EVENTS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                            Text("Complete goals to earn coin rewards", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }

            items(events.size) { index ->
                EventCard(
                    event = events[index],
                    onClaim = { viewModel.claimEvent(events[index]) }
                )
            }
        }
    }
}

@Composable
fun EventCard(event: GameEvent, onClaim: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF7C3AED)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(event.description, color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = event.progress,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFFACC15),
                    trackColor = Color.Black.copy(alpha = 0.3f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Reward: ${event.reward} Coins", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${event.current.coerceAtMost(event.goal)} / ${event.goal}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            if (event.claimed) {
                Icon(Icons.Filled.CheckCircle, contentDescription = "Claimed", tint = Color(0xFF10B981), modifier = Modifier.size(28.dp))
            } else {
                Button(
                    onClick = onClaim,
                    enabled = event.isComplete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFACC15),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFF334155),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(if (event.isComplete) "CLAIM" else "LOCKED", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
