package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.GameViewModel
import com.example.data.FirestoreRepository
import com.google.firebase.auth.FirebaseUser
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(viewModel: GameViewModel, firestoreRepository: FirestoreRepository, user: FirebaseUser?, onBackClicked: () -> Unit) {
    val notifications = user?.uid?.let { uid ->
        firestoreRepository.observeNotifications(uid).collectAsStateWithLifecycle(initialValue = emptyList()).value
    } ?: emptyList()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { user?.uid?.let(firestoreRepository::markAllNotificationsRead) }) {
                        Icon(Icons.Filled.Check, contentDescription = "Mark Read", tint = Color(0xFF60A5FA))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (notifications.isEmpty()) {
                item { Text("No notifications yet.", color = Color.Gray, modifier = Modifier.padding(16.dp)) }
            }
            items(notifications.size) { index ->
                val notification = notifications[index]
                MessageCard(
                    title = notification.title,
                    message = notification.message,
                    time = notification.createdAt?.toDate()?.toString() ?: "",
                    isUnread = !notification.read
                )
            }
        }
    }
}

@Composable
fun MessageCard(title: String, message: String, time: String, isUnread: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (isUnread) Color(0xFF1E293B) else Color(0xFF0F172A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isUnread) Color(0xFF3B82F6) else Color(0xFF334155)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Mail, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(time, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(message, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}
