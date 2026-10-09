package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdConfig
import com.example.ads.AdEnvironment
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.LocalDarkMode
import com.example.ui.theme.AppPrefs
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import com.example.viewmodel.GameViewModel
import com.example.utils.MusicManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: GameViewModel, onBackClicked: () -> Unit, onLogout: () -> Unit = {}, musicManager: MusicManager? = null) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    val darkState = LocalDarkMode.current
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(AppPrefs.isMusic(context)) }
    var hapticsEnabled by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    
    // Ads preferences
    var personalizedAds by remember { mutableStateOf(true) }
    var adEnvironment by remember { mutableStateOf(AdConfig.environment) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = colors.primaryText, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = colors.primaryText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        },
        containerColor = colors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SettingsSection("ACCOUNT") {
                    SettingsRowClickable(icon = Icons.Filled.Person, title = "Edit Profile", subtitle = "Change avatar and name") { }
                    SettingsRowClickable(icon = Icons.Filled.CloudSync, title = "Restore Cloud Save", subtitle = "Sync progress from server") { }
                    SettingsRowClickable(icon = Icons.Filled.Logout, title = "Log Out", iconTint = Color(0xFFEF4444), textColor = Color(0xFFEF4444), onClick = onLogout)
                }
            }
            item {
                SettingsSection("AUDIO & FEEDBACK") {
                    SettingsRowToggle(icon = Icons.Filled.VolumeUp, title = "Sound Effects", checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                    SettingsRowToggle(icon = Icons.Filled.MusicNote, title = "Background Music", checked = musicEnabled, onCheckedChange = {
                        musicEnabled = it
                        AppPrefs.setMusic(context, it)
                        if (it) musicManager?.start(context) else musicManager?.stop()
                    })
                    SettingsRowToggle(icon = Icons.Filled.Vibration, title = "Haptic Feedback", checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it })
                }
            }
            item {
                SettingsSection("PREFERENCES") {
                    SettingsRowToggle(
                        icon = Icons.Filled.DarkMode,
                        title = "Dark Mode",
                        subtitle = if (darkState.value) "Currently using dark theme" else "Currently using light theme",
                        checked = darkState.value,
                        onCheckedChange = {
                            darkState.value = it
                            AppPrefs.setDark(context, it)
                        }
                    )
                    SettingsRowToggle(icon = Icons.Filled.Notifications, title = "Push Notifications", checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
                    SettingsRowClickable(icon = Icons.Filled.Language, title = "Language", subtitle = "English") { }
                }
            }
            item {
                SettingsSection("ADS PREFERENCES") {
                    SettingsRowToggle(
                        icon = Icons.Filled.AdUnits,
                        title = "Personalized Ads",
                        subtitle = "Show ads based on your interests",
                        checked = personalizedAds,
                        onCheckedChange = { personalizedAds = it }
                    )
                    SettingsRowClickable(
                        icon = Icons.Filled.BugReport,
                        title = if (adEnvironment == AdEnvironment.TEST) "Test Mode (ON)" else "Test Mode (OFF)",
                        subtitle = "Switch between test and production ads",
                        onClick = {
                            val newEnv = if (adEnvironment == AdEnvironment.TEST) 
                                AdEnvironment.PRODUCTION 
                            else 
                                AdEnvironment.TEST
                            AdConfig.setEnvironment(newEnv)
                            adEnvironment = newEnv
                        }
                    )
                }
            }
            item {
                SettingsSection("ABOUT") {
                    SettingsRowClickable(icon = Icons.Filled.PrivacyTip, title = "Privacy Policy") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacy-policy-color-sort-puzz/home")))
                    }
                    SettingsRowClickable(icon = Icons.Filled.Description, title = "Terms of Service") {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacy-policy-color-sort-puzz/terms-conditions")))
                    }
                    SettingsRowClickable(icon = Icons.Filled.Delete, title = "Delete Account", iconTint = Color(0xFFEF4444), textColor = Color(0xFFEF4444)) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/privacy-policy-color-sort-puzz/delete-account-page")))
                    }
                    SettingsRowClickable(icon = Icons.Filled.Info, title = "App Version", subtitle = "1.0.0 (Build 42)") { }
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    val colors = LocalAppColors.current
    Column {
        Text(
            text = title,
            color = colors.secondaryText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.card)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsRowClickable(icon: ImageVector, title: String, subtitle: String? = null, iconTint: Color = LocalAppColors.current.primaryText, textColor: Color = LocalAppColors.current.primaryText, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, color = colors.secondaryText, fontSize = 13.sp)
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = colors.secondaryText)
    }
}

@Composable
fun SettingsRowToggle(icon: ImageVector, title: String, subtitle: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = LocalAppColors.current.primaryText, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = colors.primaryText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, color = colors.secondaryText, fontSize = 13.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF3B82F6))
        )
    }
}
