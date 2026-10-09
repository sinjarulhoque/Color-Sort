package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel, onGoogleSignIn: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)).verticalScroll(rememberScrollState()).imePadding().padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFFFACC15), modifier = Modifier.size(72.dp).background(Color(0xFF1E3A8A), CircleShape).padding(16.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text("Color Sort", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.ExtraBold)
        Text("Sign in to save your progress", color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(28.dp))
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onGoogleSignIn, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White)
            else Text("Continue with Google", color = Color(0xFF1F2937), fontWeight = FontWeight.Bold)
        }
    }
}