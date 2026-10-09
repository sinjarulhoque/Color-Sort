package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.models.GameTheme
import com.example.models.ThemeRepository
import com.example.models.UnlockType
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val unlockedThemes = stats.unlockedThemes.split(",")

    var showUnlockDialog by remember { mutableStateOf<GameTheme?>(null) }
    var unlockError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.secondaryColor)
    ) {
        currentTheme.backgroundAsset?.let { asset ->
            Image(
                painter = painterResource(id = asset),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f
            )
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("THEMES", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${stats.coins}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(ThemeRepository.themes) { theme ->
                    val isUnlocked = unlockedThemes.contains(theme.id)
                    val isSelected = currentTheme.id == theme.id

                    ThemeCard(
                        theme = theme,
                        isUnlocked = isUnlocked,
                        isSelected = isSelected,
                        onClick = {
                            if (isUnlocked) {
                                viewModel.selectTheme(theme.id)
                            } else {
                                showUnlockDialog = theme
                            }
                        }
                    )
                }
            }
        }
    }

    showUnlockDialog?.let { theme ->
        AlertDialog(
            onDismissRequest = { 
                showUnlockDialog = null
                unlockError = null
            },
            containerColor = currentTheme.cardColor,
            title = {
                Text(
                    "Unlock ${theme.name}?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    when (theme.unlockType) {
                        UnlockType.COINS -> Text("This theme costs ${theme.unlockValue} coins to unlock.", color = Color.LightGray)
                        UnlockType.LEVEL -> Text("Reach Level ${theme.unlockValue} to unlock this theme.", color = Color.LightGray)
                        UnlockType.FREE -> Text("This theme is free.", color = Color.LightGray)
                    }
                    if (unlockError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(unlockError!!, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = viewModel.unlockTheme(theme)
                        if (success) {
                            showUnlockDialog = null
                            unlockError = null
                        } else {
                            unlockError = if (theme.unlockType == UnlockType.COINS) "Not enough coins!" else "Level too low!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.accentColor)
                ) {
                    Text("UNLOCK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showUnlockDialog = null 
                    unlockError = null
                }) {
                    Text("CANCEL", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun ThemeCard(
    theme: GameTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) theme.accentColor else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = theme.primaryColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (theme.backgroundAsset != null) {
                Image(
                    painter = painterResource(id = theme.backgroundAsset),
                    contentDescription = theme.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isSelected) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = theme.accentColor)
                    } else if (!isUnlocked) {
                        Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = Color.White.copy(alpha = 0.7f))
                    }
                }

                Column {
                    Text(
                        text = theme.name.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (!isUnlocked) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (theme.unlockType == UnlockType.COINS) {
                                Icon(Icons.Filled.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("${theme.unlockValue}", color = Color.White, fontSize = 14.sp)
                            } else if (theme.unlockType == UnlockType.LEVEL) {
                                Icon(Icons.Filled.Star, contentDescription = "Level", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Lvl ${theme.unlockValue}", color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
