package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ads.AdConfig
import com.example.ads.AdService
import com.example.data.FirestoreRepository
import com.example.models.GameState
import com.example.models.Tube
import com.example.viewmodel.GameViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    levelId: Int,
    dailyDate: String? = null,
    onNextLevel: (Int) -> Unit,
    onBack: () -> Unit,
    adService: AdService,
    firestoreRepository: FirestoreRepository
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLevelId = uiState.level?.id ?: levelId

    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val stats by viewModel.playerStats.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    var showPauseDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showRestartDialog by remember { mutableStateOf(false) }
    
    // Rewarded ad states
    var showHintAd by remember { mutableStateOf(false) }
    var showUndoAd by remember { mutableStateOf(false) }
    var showLifeAd by remember { mutableStateOf(false) }
    var showGetLifeDialog by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP && uiState.gameState != GameState.COMPLETED && uiState.gameState != GameState.FAILED) {
                viewModel.pauseGame()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(levelId, dailyDate) {
        if (dailyDate != null) {
            if (uiState.dailyDate != dailyDate) {
                viewModel.loadDailyChallenge(dailyDate)
            }
        } else {
            if (uiState.level?.id != levelId) {
                viewModel.loadLevel(levelId)
            }
        }
    }

    // Helper to get activity
    val activity = context as? Activity

    // Show hint rewarded ad
    fun showHintRewardedAd() {
        if (activity == null || showHintAd) return
        showHintAd = true
        adService.showRewardedForHint(activity, onEarnedReward = {
            val sessionId = UUID.randomUUID().toString()
            firestoreRepository.claimAdReward("hint", sessionId)
                .addOnSuccessListener { result ->
                    val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                    if (amount > 0) {
                        viewModel.addHint(amount)
                    }
                    showHintAd = false
                }
                .addOnFailureListener { showHintAd = false; adError = "Reward could not be claimed. Please try again later." }
        }, onUnavailable = { showHintAd = false; adError = "Rewarded ad is unavailable right now. Please check your connection and try again." })
    }

    // Show undo rewarded ad
    fun showUndoRewardedAd() {
        if (activity == null || showUndoAd) return
        showUndoAd = true
        adService.showRewardedForUndo(activity, onEarnedReward = {
            val sessionId = UUID.randomUUID().toString()
            firestoreRepository.claimAdReward("undo", sessionId)
                .addOnSuccessListener { result ->
                    val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                    if (amount > 0) {
                        viewModel.addUndo(amount)
                    }
                    showUndoAd = false
                }
                .addOnFailureListener { showUndoAd = false; adError = "Reward could not be claimed. Please try again later." }
        }, onUnavailable = { showUndoAd = false; adError = "Rewarded ad is unavailable right now. Please check your connection and try again." })
    }

    // Show life rewarded ad
    fun showLifeRewardedAd() {
        if (activity == null || showLifeAd) return
        showLifeAd = true
        adService.showRewardedForLife(activity, onEarnedReward = {
            val sessionId = UUID.randomUUID().toString()
            firestoreRepository.claimAdReward("life", sessionId)
                .addOnSuccessListener { result ->
                    val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                    if (amount > 0) {
                        viewModel.addLife(amount)
                    }
                    showLifeAd = false
                }
                .addOnFailureListener { showLifeAd = false; adError = "Reward could not be claimed. Please try again later." }
        }, onUnavailable = { showLifeAd = false; adError = "Rewarded ad is unavailable right now. Please check your connection and try again." })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(currentTheme.secondaryColor)
    ) {
        currentTheme.backgroundAsset?.let { asset ->
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
                val titleText = if (dailyDate != null) "DAILY CHALLENGE" else "LEVEL $currentLevelId"
                CenterAlignedTopAppBar(
                    title = { Text(titleText, color = currentTheme.textColor, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(
                            onClick = { if (uiState.gameState == GameState.IDLE || uiState.gameState == GameState.SELECTING_TUBE) showLeaveDialog = true else onBack() },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .background(currentTheme.primaryColor, RoundedCornerShape(8.dp))
                                .size(40.dp)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = currentTheme.textColor)
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFBBF24), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("${stats.coins}", color = currentTheme.textColor, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { viewModel.pauseGame(); showPauseDialog = true }) {
                            Icon(Icons.Filled.Pause, contentDescription = "Pause", tint = currentTheme.textColor)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Timer Box
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .background(currentTheme.cardColor, RoundedCornerShape(8.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val mins = uiState.timeSeconds / 60
                    val secs = uiState.timeSeconds % 60
                    Text(
                        text = String.format("⏱ %02d:%02d", mins, secs),
                        color = currentTheme.textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text("MOVES: ${uiState.moves}", color = currentTheme.textColor, fontWeight = FontWeight.Bold)
                    Text("TIME: ${String.format("%02d:%02d", uiState.timeSeconds / 60, uiState.timeSeconds % 60)}", color = currentTheme.textColor, fontWeight = FontWeight.Bold)
                    Text("LIVES: ${stats.lives}/5", color = currentTheme.textColor, fontWeight = FontWeight.Bold)
                }

            // Power-ups
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Undo Button
                Button(
                    onClick = { 
                        if (uiState.undosAvailable > 0) {
                            viewModel.undoMove()
                        } else {
                            showUndoRewardedAd()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.undosAvailable > 0) Color(0xFFFBBF24) else Color(0xFF9333EA)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    enabled = uiState.undosAvailable > 0 || adService.isRewardedAvailable()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    if (uiState.undosAvailable > 0) {
                        Text("${uiState.undosAvailable}", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Row {
                            Icon(Icons.Filled.VideoCall, contentDescription = "Ad", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("FREE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                // Hint Button
                Button(
                    onClick = { 
                        if (uiState.hintsAvailable > 0) {
                            viewModel.showHint()
                        } else {
                            showHintRewardedAd()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (uiState.hintsAvailable > 0) Color(0xFFFBBF24) else Color(0xFF9333EA)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    enabled = uiState.hintsAvailable > 0 || adService.isRewardedAvailable()
                ) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = "Hint", tint = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    if (uiState.hintsAvailable > 0) {
                        Text("${uiState.hintsAvailable}", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Row {
                            Icon(Icons.Filled.VideoCall, contentDescription = "Ad", tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("FREE", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                // Extra Tube Button
                Button(
                    onClick = { viewModel.useExtraTube() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Extra Tube", tint = Color.Black)
                    Icon(Icons.Filled.Science, contentDescription = "Tube", tint = Color.Black)
                    Spacer(Modifier.width(4.dp))
                    Text("${uiState.extraTubesAvailable}", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tubes Grid
            uiState.level?.let { level ->
                val chunkedTubes = level.tubes.chunked(level.tubes.size / 2 + level.tubes.size % 2)
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    chunkedTubes.forEach { rowTubes ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                            rowTubes.forEach { tube ->
                                val isSelected = uiState.selectedTubeId == tube.id
                                val isAnimatingSource = uiState.animatingSource == tube.id
                                
                                TubeView(
                                    tube = tube,
                                    isSelected = isSelected,
                                    isAnimatingSource = isAnimatingSource || uiState.hintSourceId == tube.id || uiState.hintDestinationId == tube.id,
                                    onClick = { viewModel.onTubeSelected(tube.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom space
        }

        if (uiState.gameState == GameState.COMPLETED) {
            LevelCompleteDialog(
                levelId = currentLevelId,
                moves = uiState.moves,
                timeSeconds = uiState.timeSeconds,
                earnedStars = uiState.earnedStars,
                rewardCoins = uiState.rewardCoins,
                rewardXp = uiState.rewardXp,
                onNext = { onNextLevel(currentLevelId + 1) },
                onHome = onBack,
                adService = adService,
                firestoreRepository = firestoreRepository,
                viewModel = viewModel,
                onAdError = { adError = it }
            )
        }
        if (uiState.gameState == GameState.FAILED) {
            LevelFailedDialog(
                stats = stats,
                uiState = uiState,
                onTryAgain = { viewModel.restartLevel() },
                onGetLife = { /* handled in dialog */ },
                onHome = onBack,
                adService = adService,
                firestoreRepository = firestoreRepository,
                viewModel = viewModel,
                onAdError = { adError = it }
            )
        }
        uiState.hintMessage?.let { message ->
            AlertDialog(onDismissRequest = viewModel::clearHint, text = { Text(message) }, confirmButton = { TextButton(onClick = viewModel::clearHint) { Text("OK") } })
        }
        if (showPauseDialog) {
            AlertDialog(
                onDismissRequest = {}, title = { Text("GAME PAUSED") },
                confirmButton = { TextButton(onClick = { showPauseDialog = false; viewModel.resumeGame() }) { Text("RESUME") } },
                dismissButton = { Row { TextButton(onClick = { showRestartDialog = true }) { Text("RESTART") }; TextButton(onClick = onBack) { Text("EXIT") } } }
            )
        }
        if (showLeaveDialog) {
            AlertDialog(onDismissRequest = { showLeaveDialog = false }, title = { Text("Leave this level?") },
                confirmButton = { TextButton(onClick = onBack) { Text("LEAVE") } },
                dismissButton = { TextButton(onClick = { showLeaveDialog = false }) { Text("CONTINUE") } })
        }
        if (showRestartDialog) {
            AlertDialog(onDismissRequest = { showRestartDialog = false }, title = { Text("RESTART LEVEL?") }, text = { Text("Your current progress will be lost.") },
                confirmButton = { TextButton(onClick = { showRestartDialog = false; showPauseDialog = false; viewModel.restartLevel() }) { Text("RESTART") } },
                dismissButton = { TextButton(onClick = { showRestartDialog = false }) { Text("CANCEL") } })
        }
        if (adError != null) {
            AlertDialog(
                onDismissRequest = { adError = null },
                confirmButton = { TextButton(onClick = { adError = null }) { Text("OK") } },
                title = { Text("Unable to collect reward") },
                text = { Text(adError!!) }
            )
        }
    } // closes Scaffold content lambda
    } // closes Box
} // closes GameScreen

@Composable
fun TubeView(
    tube: Tube,
    isSelected: Boolean,
    isAnimatingSource: Boolean,
    onClick: () -> Unit
) {
    val translationY by animateFloatAsState(
        targetValue = if (isSelected || isAnimatingSource) -30f else 0f,
        label = "tube_lift"
    )
    val rotationZ by animateFloatAsState(
        targetValue = if (isAnimatingSource) 45f else 0f,
        label = "tube_pour"
    )

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(160.dp)
            .graphicsLayer {
                this.translationY = translationY
                this.rotationZ = rotationZ
            }
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .border(2.dp, if (isSelected) Color.White else Color.Gray, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Fill empty space
            val emptySpaces = tube.capacity - tube.colors.size
            repeat(emptySpaces) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f))
            }
            // Fill colors from top to bottom
            tube.colors.reversed().forEach { color ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(color.color)
                )
            }
        }
        
        // Glass reflection effect
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .align(Alignment.CenterStart)
                .padding(start = 4.dp, top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.3f))
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp, top = 8.dp, bottom = 8.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(Color.White.copy(alpha = 0.1f))
        )
        // Rim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .background(Color.White.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun LevelCompleteDialog(
    levelId: Int,
    moves: Int,
    timeSeconds: Int,
    earnedStars: Int = 1,
    rewardCoins: Int = 0,
    rewardXp: Int = 0,
    onNext: () -> Unit,
    onHome: () -> Unit,
    adService: AdService,
    firestoreRepository: FirestoreRepository,
    viewModel: GameViewModel,
    onAdError: (String) -> Unit = {}
) {
    var revealedStars by remember { mutableStateOf(0) }
    var showBonusAd by remember { mutableStateOf(false) }
    var bonusClaimed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    
    LaunchedEffect(earnedStars) {
        revealedStars = 0
        repeat(earnedStars.coerceIn(1, 6)) {
            delay(260)
            revealedStars++
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("LEVEL $levelId", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .background(Color(0xFFEF4444), RoundedCornerShape(percent = 50))
                    .padding(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text("COMPLETED!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(6) { index ->
                    Icon(Icons.Filled.Star, contentDescription = "Star ${index + 1}", tint = if (index < revealedStars) Color(0xFFFBBF24) else Color(0xFF475569), modifier = Modifier.size(38.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.MonetizationOn, contentDescription = "Coins", tint = Color(0xFFFBBF24), modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("+$rewardCoins", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val mins = timeSeconds / 60
            val secs = timeSeconds % 60
            Text("Time: ${String.format("%02d:%02d", mins, secs)} • Moves: $moves", color = Color.LightGray, fontSize = 16.sp)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Double Reward Button
            if (!bonusClaimed && adService.isRewardedAvailable() && activity != null) {
                Button(
                    onClick = {
                        showBonusAd = true
                        adService.showRewardedForBonusReward(activity!!, onEarnedReward = {
                            val sessionId = UUID.randomUUID().toString()
                            firestoreRepository.claimAdReward("bonus_reward", sessionId, levelId)
                                .addOnSuccessListener { result ->
                                    val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                                    if (amount > 0) {
                                        viewModel.addCoins(amount)
                                        bonusClaimed = true
                                    }
                                    showBonusAd = false
                                }
                                .addOnFailureListener { showBonusAd = false; onAdError("Reward could not be claimed. Please try again later.") }
                        }, onUnavailable = { showBonusAd = false; onAdError("Rewarded ad is unavailable right now. Please check your connection and try again.") })
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
                ) {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.MonetizationOn, contentDescription = "Coins", tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("DOUBLE REWARD", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("+$rewardCoins", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                        if (showBonusAd) {
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Filled.HourglassTop, contentDescription = "Loading", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                Text("Watch ad to double your coins", color = Color(0xFFD8B4FE), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF84CC16))
            ) {
                Text("NEXT", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onHome,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
            ) {
                Text("HOME", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun LevelFailedDialog(
    stats: com.example.data.PlayerStats,
    uiState: com.example.viewmodel.GameUiState,
    onTryAgain: () -> Unit,
    onGetLife: () -> Unit,
    onHome: () -> Unit,
    adService: AdService,
    firestoreRepository: FirestoreRepository,
    viewModel: GameViewModel,
    onAdError: (String) -> Unit = {}
) {
    var showContinueAd by remember { mutableStateOf(false) }
    var showLifeAd by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    
    AlertDialog(
        onDismissRequest = {},
        title = { Text("LEVEL FAILED", fontWeight = FontWeight.Bold) },
        text = { Text("No legal moves remain. Lives: ${stats.lives}/5\nMoves: ${uiState.moves}\nTime: ${String.format("%02d:%02d", uiState.timeSeconds / 60, uiState.timeSeconds % 60)}") },
        confirmButton = { 
            // TRY AGAIN - loses a life
            TextButton(onClick = { 
                viewModel.restartLevel()
                onTryAgain()
            }) { Text("TRY AGAIN") } 
        },
        dismissButton = { 
            // HOME
            TextButton(onClick = onHome) { Text("HOME") } 
        }
    )
    
    // Show additional options below the dialog
    if (stats.lives < 5) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // GET LIFE
            Button(
                onClick = {
                    if (activity != null && adService.isRewardedAvailable()) {
                        showLifeAd = true
                        adService.showRewardedForLife(activity!!, onEarnedReward = {
                            val sessionId = UUID.randomUUID().toString()
                            firestoreRepository.claimAdReward("life", sessionId)
                                .addOnSuccessListener { result ->
                                    val amount = ((result.data as? Map<*, *>)?.get("amount") as? Number)?.toInt() ?: 0
                                    if (amount > 0) {
                                        viewModel.addLife(amount)
                                    }
                                    showLifeAd = false
                                }
                                .addOnFailureListener { showLifeAd = false; onAdError("Reward could not be claimed. Please try again later.") }
                        }, onUnavailable = { showLifeAd = false; onAdError("Rewarded ad is unavailable right now. Please check your connection and try again.") })
                    } else {
                        onGetLife()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                enabled = activity != null && adService.isRewardedAvailable()
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, contentDescription = "Life", tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("GET 1 LIFE", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (showLifeAd) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.HourglassTop, contentDescription = "Loading", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Text("Watch ad to get +1 life", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
