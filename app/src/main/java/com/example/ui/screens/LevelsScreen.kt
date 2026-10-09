package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsScreen(viewModel: GameViewModel, onBackClicked: () -> Unit, onLevelSelected: (Int) -> Unit) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val progress by viewModel.allLevelProgress.collectAsStateWithLifecycle()
    val progressByLevel = progress.associateBy { it.levelId }
    val highestUnlocked = stats.highestUnlockedLevel

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level Select", color = Color.White, fontWeight = FontWeight.Bold) },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            items(100) { index ->
                val level = index + 1
                val isUnlocked = level <= highestUnlocked
                LevelCard(level = level, isUnlocked = isUnlocked, stars = progressByLevel[level]?.stars ?: 0) {
                    if (isUnlocked) onLevelSelected(level)
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: Int, isUnlocked: Boolean, stars: Int = 0, onClick: () -> Unit) {
    val bgColor = if (isUnlocked) {
        Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B)))
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(2.dp, if (isUnlocked) Color(0xFF60A5FA) else Color(0xFF475569), RoundedCornerShape(16.dp))
            .clickable(enabled = isUnlocked, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isUnlocked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$level", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 4.dp)) {
                    repeat(6) { index ->
                        Icon(Icons.Filled.Star, contentDescription = null, tint = if (index < stars) Color(0xFFFBBF24) else Color.Gray, modifier = Modifier.size(12.dp))
                    }
                }
            }
        } else {
            Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = Color.Gray, modifier = Modifier.size(36.dp))
        }
    }
}
