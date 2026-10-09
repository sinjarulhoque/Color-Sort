package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoCall
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
import com.example.ads.AdConfig
import com.example.ads.AdService
import com.example.data.FirestoreRepository
import com.example.ui.theme.AppPrefs
import com.example.ui.theme.LocalAppColors
import com.example.ui.theme.rememberCountdown
import com.example.viewmodel.GameViewModel
import com.google.firebase.auth.FirebaseUser
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.UUID

data class DailyRewardItem(val day: String, val coins: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLoginScreen(
    viewModel: GameViewModel,
    firestoreRepository: FirestoreRepository,
    adService: AdService,
    user: FirebaseUser?,
    onBackClicked: () -> Unit
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val loginState = user?.uid?.let { firestoreRepository.observeDailyLogin(it) }
        ?.collectAsStateWithLifecycle(initialValue = com.example.data.DailyLoginState())?.value
        ?: com.example.data.DailyLoginState()
    val remoteRewards by firestoreRepository.observeDailyLoginRewards().collectAsStateWithLifecycle(initialValue = emptyList())
    var errorDialog by remember { mutableStateOf<String?>(null) }
    var showDoubleBonus by remember { mutableStateOf(false) }
    var doubleBonusAdLoading by remember { mutableStateOf(false) }
    val activity = context as? Activity

    // Local (offline-capable) state for claims and timers.
    var claimedDays by remember { mutableStateOf(AppPrefs.getDailyClaimedDays(context)) }
    var lastClaim by remember { mutableStateOf(AppPrefs.getLastDailyReward(context)) }
    val availableDay = if (AppPrefs.isSameDay(lastClaim)) 0 else (claimedDays.maxOrNull() ?: 0) + 1

    val rewards: List<DailyRewardItem> = if (remoteRewards.isNotEmpty()) {
        remoteRewards.map { DailyRewardItem(it.day.toString(), it.coins.toInt()) }
    } else {
        listOf(100, 150, 200, 250, 300, 400, 500).mapIndexed { i, c -> DailyRewardItem("${i + 1}", c) }
    }

    val nextMid = AppPrefs.nextMidnight()
    val resetCountdown = rememberCountdown(nextMid)
    val todayClaimed = AppPrefs.isSameDay(lastClaim)

    fun claimDay(day: Int, coins: Int) {
        // Record locally first so the timer + state work even without network.
        claimedDays = claimedDays.toMutableSet().apply { add(day) }
        lastClaim = System.currentTimeMillis()
        AppPrefs.addDailyClaimedDay(context, day)
        AppPrefs.setLastDailyReward(context, lastClaim)
        viewModel.addCoins(coins)
        // Best-effort remote sync.
        firestoreRepository.claimDailyLoginReward(day.toLong())
            .addOnFailureListener { errorDialog = it.message ?: "Reward could not be claimed." }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Login", color = colors.primaryText, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login Streak", color = colors.primaryText, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text("Come back every day for better rewards!", color = colors.secondaryText, fontSize = 14.sp)
            if (todayClaimed) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Next reward in $resetCountdown", color = colors.accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(rewards.size) { index ->
                    val reward = rewards[index]
                    val day = reward.day.toInt()
                    val state = when {
                        claimedDays.contains(day) -> "CLAIMED"
                        day == availableDay -> "AVAILABLE"
                        else -> "LOCKED"
                    }
                    val bgColor = when (state) {
                        "CLAIMED" -> colors.success
                        "AVAILABLE" -> colors.accent
                        else -> colors.card
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .clickable(enabled = state == "AVAILABLE") { claimDay(day, reward.coins) },
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Day $day", color = colors.primaryText, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (state == "CLAIMED") {
                                Box(modifier = Modifier.size(32.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = colors.success)
                                }
                            } else {
                                Icon(Icons.Filled.Stars, contentDescription = null, tint = if (state == "LOCKED") colors.secondaryText else colors.warning, modifier = Modifier.size(32.dp))
                                Text("+${reward.coins}", color = if (state == "LOCKED") colors.secondaryText else colors.primaryText, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Double Bonus Section for today's reward if already claimed
            if (todayClaimed) {
                Spacer(modifier = Modifier.height(16.dp))
                val limit = AdConfig.rewardedDailyLimits["daily_bonus"] ?: 1
                if (limit > 0) {
                    Button(
                        onClick = {
                            if (activity != null && adService.isRewardedAvailable()) {
                                doubleBonusAdLoading = true
                                val sessionId = UUID.randomUUID().toString()
                                adService.showRewardedForDailyBonus(activity, onEarnedReward = {
                                    firestoreRepository.claimDailyLoginRewardWithMultiplier(availableDay.toLong(), 2, sessionId)
                                        .addOnSuccessListener { result ->
                                            val coins = ((result.data as? Map<*, *>)?.get("coins") as? Number)?.toInt() ?: 0
                                            if (coins > 0) {
                                                viewModel.addCoins(coins)
                                                showDoubleBonus = true
                                            }
                                            doubleBonusAdLoading = false
                                        }
                                        .addOnFailureListener {
                                            errorDialog = it.message ?: "Could not claim double bonus"
                                            doubleBonusAdLoading = false
                                        }
                                }, onUnavailable = {
                                    errorDialog = "Rewarded ad is unavailable right now. Please check your connection and try again."
                                    doubleBonusAdLoading = false
                                })
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                        enabled = activity != null && adService.isRewardedAvailable() && !doubleBonusAdLoading
                    ) {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Stars, contentDescription = "Bonus", tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("2X DAILY BONUS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            if (doubleBonusAdLoading) {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Filled.HourglassTop, contentDescription = "Loading", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    Text("Watch ad to double today's reward", color = Color(0xFFD8B4FE), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                } else {
                    Text("Daily bonus already claimed", color = colors.secondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }

            if (showDoubleBonus) {
                Text("Double bonus claimed! Coins added.", color = colors.success, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }

            if (showDoubleBonus) {
                Text("Double bonus claimed! Coins added.", color = colors.success, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            }
        }
    }

    if (errorDialog != null) {
        AlertDialog(
            onDismissRequest = { errorDialog = null },
            confirmButton = {
                TextButton(onClick = { errorDialog = null }) { Text("OK", color = colors.accent, fontWeight = FontWeight.Bold) }
            },
            title = { Text("Unable to collect reward", color = colors.primaryText, fontWeight = FontWeight.Bold) },
            text = { Text(errorDialog!!, color = colors.secondaryText) },
            containerColor = colors.surface
        )
    }
}
