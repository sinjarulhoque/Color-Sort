package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeCoinsScreen(viewModel: GameViewModel, firestoreRepository: FirestoreRepository, adService: AdService, onBackClicked: () -> Unit) {
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val activity = context as? Activity
    var isWatching by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var errorDialog by remember { mutableStateOf<String?>(null) }
    var claimsUsed by remember { mutableStateOf(0) }
    var claimsLoaded by remember { mutableStateOf(false) }

    // Local cooldown (works without network / AdMob).
    val cooldownTarget = AppPrefs.freeCoinsTarget(context)
    val cooldown = rememberCountdown(cooldownTarget)
    val onCooldown = cooldownTarget != 0L

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val db = FirebaseFirestore.getInstance()
        val today = java.time.LocalDate.now().toString()
        db.collection("users").document(uid).collection("adRewards")
            .whereEqualTo("date", today)
            .whereEqualTo("rewardType", "coins")
            .get()
            .addOnSuccessListener { snapshot ->
                claimsUsed = snapshot.size()
                claimsLoaded = true
            }
            .addOnFailureListener {
                claimsLoaded = true
            }
    }

    val limit = AdConfig.rewardedDailyLimits["coins"] ?: 5
    val remaining = (limit - claimsUsed).coerceAtLeast(0)
    val canClaim = remaining > 0 && !onCooldown

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Free Coins", color = colors.primaryText, fontWeight = FontWeight.Bold) }, navigationIcon = {
                IconButton(onClick = onBackClicked) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = colors.primaryText) }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface))
        }, containerColor = colors.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = colors.warning, modifier = Modifier.size(88.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Watch a rewarded ad", color = colors.primaryText, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text("Earn 25 coins after the ad confirms your reward.", color = colors.secondaryText)
            Spacer(modifier = Modifier.height(8.dp))

            if (claimsLoaded) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text("FREE COINS", color = colors.primaryText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Text("$claimsUsed / $limit USED", color = colors.warning, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(16.dp))
                    Text("Remaining: $remaining", color = colors.success, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (onCooldown) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.card)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Next free coin in", color = colors.secondaryText, fontSize = 14.sp)
                        Text(cooldown, color = colors.accent, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                enabled = !isWatching && canClaim && adService.isRewardedAvailable() && activity != null,
                onClick = {
                    val currentActivity = activity ?: return@Button
                    isWatching = true
                    // Start local cooldown immediately so the timer is accurate even if the ad fails.
                    AppPrefs.setLastFreeCoins(context, System.currentTimeMillis())
                    val sessionId = UUID.randomUUID().toString()
                    adService.showRewardedForCoins(currentActivity, onEarnedReward = {
                        firestoreRepository.claimAdReward("coins", sessionId)
                            .addOnSuccessListener { result ->
                                val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                                if (amount > 0) viewModel.addCoins(amount)
                                message = if (amount > 0) "+$amount coins added" else "Reward already claimed"
                                claimsUsed++
                                isWatching = false
                            }
                            .addOnFailureListener { errorDialog = "Reward could not be claimed. Please try again later."; isWatching = false }
                    }, onUnavailable = { errorDialog = "Rewarded ad is unavailable right now. Please check your connection and try again."; isWatching = false })
                }, modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (isWatching) "Watching ad..." else if (canClaim) "WATCH AD +25 COINS" else "DAILY LIMIT REACHED", fontWeight = FontWeight.Bold)
            }

            if (!canClaim && !onCooldown) {
                Text("Daily free coin limit reached. Resets tomorrow.", color = colors.error, modifier = Modifier.padding(top = 8.dp))
            }

            message?.let { Text(it, color = colors.success, modifier = Modifier.padding(top = 16.dp)) }
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
