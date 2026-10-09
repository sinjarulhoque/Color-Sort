package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.GameViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit
) {
    val allRecords by viewModel.allDailyRecords.collectAsStateWithLifecycle()
    val theme by viewModel.currentTheme.collectAsStateWithLifecycle()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()

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
                    title = { Text("HISTORY", color = theme.textColor, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = theme.textColor)
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
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Month Navigator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month", tint = theme.accentColor)
                    }
                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")).uppercase(),
                        color = theme.textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        enabled = currentMonth.isBefore(YearMonth.now())
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month", tint = if (currentMonth.isBefore(YearMonth.now())) theme.accentColor else Color.Transparent)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Weekday Headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            color = theme.textColor.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val daysInMonth = currentMonth.lengthOfMonth()
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
                val totalCells = daysInMonth + firstDayOfWeek

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(totalCells) { index ->
                        if (index < firstDayOfWeek) {
                            Box(modifier = Modifier.aspectRatio(1f))
                        } else {
                            val day = index - firstDayOfWeek + 1
                            val date = currentMonth.atDay(day)
                            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                            
                            val record = allRecords.find { it.dateString == dateString }
                            val isFuture = date.isAfter(today)
                            val isToday = date == today

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(if (isToday) theme.primaryColor.copy(alpha = 0.5f) else Color.Transparent)
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) theme.accentColor else Color.White.copy(alpha = 0.1f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$day",
                                        color = if (isFuture) theme.textColor.copy(alpha = 0.3f) else theme.textColor,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    if (isFuture) {
                                        Icon(Icons.Filled.Lock, contentDescription = null, tint = theme.textColor.copy(alpha = 0.3f), modifier = Modifier.size(12.dp))
                                    } else if (record != null) {
                                        if (record.isCompleted) {
                                            Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                                        } else {
                                            Icon(Icons.Filled.Circle, contentDescription = "Played", tint = theme.accentColor, modifier = Modifier.size(8.dp))
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = theme.cardColor),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Completed", color = theme.textColor)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Circle, contentDescription = null, tint = theme.accentColor, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Attempted", color = theme.textColor)
                        }
                    }
                }
            }
        }
    }
}
