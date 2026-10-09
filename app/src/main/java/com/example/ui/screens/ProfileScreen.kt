package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GameViewModel
import com.example.data.FirestoreRepository
import com.example.ui.components.ProfileAvatar
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: GameViewModel, firestoreRepository: FirestoreRepository? = null, onBackClicked: () -> Unit, user: FirebaseUser? = null) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val cloudProfile by user?.uid?.let { firestoreRepository?.observePublicProfile(it) }
        ?.collectAsStateWithLifecycle(initialValue = null)
        ?: remember { mutableStateOf(null) }
    val name = cloudProfile?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@") ?: "Player"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Avatar
            ProfileAvatar(
                seed = name,
                index = stats.avatarIndex,
                size = 100.dp,
                modifier = Modifier.border(4.dp, Color(0xFFFCD34D), CircleShape)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(name, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cloudProfile?.countryCode?.takeIf { it.isNotBlank() } ?: "Country not set", color = Color.Gray, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Filled.MilitaryTech, title = "Level", value = "${cloudProfile?.level ?: stats.highestUnlockedLevel}")
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Filled.Star, title = "Total XP", value = "${cloudProfile?.xp ?: stats.xp}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Filled.LocalFireDepartment, title = "Streak", value = "${stats.dailyStreak} Days")
                StatCard(modifier = Modifier.weight(1f), icon = Icons.Filled.EmojiEvents, title = "Achievements", value = "12/50")
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, title: String, value: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
