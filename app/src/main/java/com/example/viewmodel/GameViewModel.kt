package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.LevelProgress
import com.example.data.PlayerStats
import com.example.models.GameState
import com.example.models.Level
import com.example.models.LevelGenerator
import com.example.models.LiquidColor
import com.example.models.Move
import com.example.models.Tube
import com.example.utils.SoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.models.GameTheme
import com.example.models.ThemeRepository
import com.example.models.UnlockType
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import com.example.data.DailyRecord
import com.example.data.FirestoreRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.UUID

enum class GameMode { NORMAL, DAILY_CHALLENGE }

data class GameUiState(
    val level: Level? = null,
    val gameState: GameState = GameState.IDLE,
    val selectedTubeId: Int? = null,
    val moves: Int = 0,
    val moveHistory: List<Move> = emptyList(),
    val animatingSource: Int? = null,
    val animatingDest: Int? = null,
    val timeSeconds: Int = 0,
    val gameMode: GameMode = GameMode.NORMAL,
    val dailyDate: String? = null
    ,val undoHistory: List<Level> = emptyList()
    ,val hintSourceId: Int? = null
    ,val hintDestinationId: Int? = null
    ,val hintMessage: String? = null
    ,val hintsAvailable: Int = 5
    ,val undosAvailable: Int = 5
    ,val extraTubesAvailable: Int = 2
    ,val earnedStars: Int = 0
    ,val rewardCoins: Int = 0
    ,val rewardXp: Int = 0
)

class GameViewModel(
    private val repository: GameRepository,
    private val soundManager: SoundManager,
    private val firestoreRepository: FirestoreRepository? = null
) : ViewModel() {

    val playerStats: StateFlow<PlayerStats> = repository.playerStats.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), PlayerStats()
    )
    val allLevelProgress = repository.levelProgress.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    
    val currentTheme: StateFlow<GameTheme> = playerStats.map {
        ThemeRepository.getThemeById(it.selectedThemeId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeRepository.themes.first())

    val allDailyRecords: StateFlow<List<DailyRecord>> = repository.getAllDailyRecords().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Active game state for mid-level resume
    val activeGameState = repository.getActiveGameState().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    fun getDailyRecordFlow(date: String) = repository.getDailyRecord(date)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionId: String? = null
    private var autoSaveJob: kotlinx.coroutines.Job? = null

    fun selectTheme(themeId: String) {
        viewModelScope.launch {
            val stats = playerStats.value
            if (stats.unlockedThemes.split(",").contains(themeId)) {
                repository.updateStats(stats.copy(selectedThemeId = themeId))
            }
        }
    }

    fun unlockTheme(theme: GameTheme): Boolean {
        val stats = playerStats.value
        val unlocked = stats.unlockedThemes.split(",").toMutableList()
        if (unlocked.contains(theme.id)) return true

        return when (theme.unlockType) {
            UnlockType.FREE -> {
                unlocked.add(theme.id)
                viewModelScope.launch {
                    repository.updateStats(stats.copy(unlockedThemes = unlocked.joinToString(","), selectedThemeId = theme.id))
                }
                true
            }
            UnlockType.COINS -> {
                if (stats.coins >= theme.unlockValue) {
                    unlocked.add(theme.id)
                    viewModelScope.launch {
                        repository.updateStats(
                            stats.copy(
                                coins = stats.coins - theme.unlockValue,
                                unlockedThemes = unlocked.joinToString(","),
                                selectedThemeId = theme.id
                            )
                        )
                    }
                    soundManager.playComplete() // Celebrate unlock
                    true
                } else false
            }
            UnlockType.LEVEL -> {
                if (stats.highestUnlockedLevel >= theme.unlockValue) {
                    unlocked.add(theme.id)
                    viewModelScope.launch {
                        repository.updateStats(stats.copy(unlockedThemes = unlocked.joinToString(","), selectedThemeId = theme.id))
                    }
                    soundManager.playComplete()
                    true
                } else false
            }
        }
    }

    // Save current game state for resume later
    private fun saveActiveGameState() {
        val state = _uiState.value
        val level = state.level ?: return
        if (state.gameState == GameState.COMPLETED || state.gameState == GameState.FAILED) {
            clearSavedGame()
            return
        }
        viewModelScope.launch {
            val activeState = com.example.data.ActiveGameState(
                levelId = level.id,
                dailyDate = state.dailyDate,
                tubesJson = repository.serializeTubes(level.tubes),
                moves = state.moves,
                timeSeconds = state.timeSeconds,
                hintsAvailable = state.hintsAvailable,
                undosAvailable = state.undosAvailable,
                extraTubesAvailable = state.extraTubesAvailable,
                selectedThemeId = playerStats.value.selectedThemeId,
                undoHistoryJson = repository.serializeLevels(state.undoHistory),
                lastSaved = System.currentTimeMillis()
            )
            repository.saveActiveGameState(activeState)
        }
    }

    // Clear saved game state (on completion or new game)
    private fun clearSavedGame() {
        viewModelScope.launch {
            repository.clearActiveGameState()
        }
    }

    // Restore saved game state if exists for this level
    private fun restoreActiveGameState(levelId: Int, dailyDate: String?): Boolean {
        val saved = activeGameState.value
        if (saved == null) return false
        if (saved.levelId != levelId) return false
        if (saved.dailyDate != dailyDate) return false
        
        val tubes = repository.deserializeTubes(saved.tubesJson)
        val undoHistory = repository.deserializeLevels(saved.undoHistoryJson)
        
        val stats = playerStats.value
        val level = if (dailyDate != null) LevelGenerator.getDailyLevel(dailyDate) else LevelGenerator.getLevel(levelId)
        
        _uiState.update {
            it.copy(
                level = level.copy(tubes = tubes),
                gameState = GameState.IDLE,
                selectedTubeId = null,
                moves = saved.moves,
                moveHistory = emptyList(), // Don't restore move history for simplicity
                animatingSource = null,
                animatingDest = null,
                timeSeconds = saved.timeSeconds,
                gameMode = if (dailyDate != null) GameMode.DAILY_CHALLENGE else GameMode.NORMAL,
                dailyDate = dailyDate,
                undoHistory = undoHistory,
                hintSourceId = null,
                hintDestinationId = null,
                hintMessage = null,
                earnedStars = 0,
                rewardCoins = 0,
                rewardXp = 0,
                hintsAvailable = saved.hintsAvailable,
                undosAvailable = saved.undosAvailable,
                extraTubesAvailable = saved.extraTubesAvailable
            )
        }
        return true
    }

    fun loadLevel(levelId: Int) {
        val restored = restoreActiveGameState(levelId, null)
        if (!restored) {
            val level = LevelGenerator.getLevel(levelId)
            val stats = playerStats.value
            _uiState.update { 
                it.copy(
                    level = level,
                    gameState = GameState.IDLE,
                    selectedTubeId = null,
                    moves = 0,
                    moveHistory = emptyList(),
                    animatingSource = null,
                    animatingDest = null,
                    timeSeconds = 0,
                    gameMode = GameMode.NORMAL,
                    dailyDate = null,
                    undoHistory = emptyList(), hintSourceId = null, hintDestinationId = null,
                    hintMessage = null, earnedStars = 0, rewardCoins = 0, rewardXp = 0,
                    hintsAvailable = stats.hints,
                    undosAvailable = stats.undos,
                    extraTubesAvailable = 2 + stats.extraTubes
                )
            }
        }
        startTimer()
        startCloudSession(levelId)
        startAutoSave()
    }

    fun loadDailyChallenge(dateString: String) {
        val restored = restoreActiveGameState(9999, dateString)
        if (!restored) {
            val level = LevelGenerator.getDailyLevel(dateString)
            val stats = playerStats.value
            _uiState.update { 
                it.copy(
                    level = level,
                    gameState = GameState.IDLE,
                    selectedTubeId = null,
                    moves = 0,
                    moveHistory = emptyList(),
                    animatingSource = null,
                    animatingDest = null,
                    timeSeconds = 0,
                    gameMode = GameMode.DAILY_CHALLENGE,
                    dailyDate = dateString,
                    undoHistory = emptyList(), hintSourceId = null, hintDestinationId = null,
                    hintMessage = null, earnedStars = 0, rewardCoins = 0, rewardXp = 0,
                    hintsAvailable = stats.hints,
                    undosAvailable = stats.undos,
                    extraTubesAvailable = 2 + stats.extraTubes
                )
            }
        }
        viewModelScope.launch {
            val existing = repository.getDailyRecord(dateString).firstOrNull()
            if (existing == null) {
                repository.insertDailyRecord(
                    DailyRecord(
                        dateString = dateString,
                        score = 0,
                        moves = 0,
                        timeSeconds = 0,
                        isCompleted = false,
                        rewardClaimed = false
                    )
                )
            }
        }
        startTimer()
        startCloudSession(9999)
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (state.gameState == GameState.IDLE || state.gameState == GameState.SELECTING_TUBE) {
                    _uiState.update { it.copy(timeSeconds = it.timeSeconds + 1) }
                }
            }
        }
    }

    private fun startAutoSave() {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            while (true) {
                delay(5000) // Save every 5 seconds during active play
                val state = _uiState.value
                if (state.gameState == GameState.IDLE || state.gameState == GameState.SELECTING_TUBE) {
                    saveActiveGameState()
                }
            }
        }
    }

    fun onTubeSelected(tubeId: Int) {
        val state = _uiState.value
        if (state.gameState != GameState.IDLE && state.gameState != GameState.SELECTING_TUBE) return
        val level = state.level ?: return

        if (state.selectedTubeId == null) {
            // Select first tube if it's not empty
            val tube = level.tubes.find { it.id == tubeId }
            if (tube != null && !tube.isEmpty) {
                soundManager.playSelect()
                _uiState.update { it.copy(selectedTubeId = tubeId, gameState = GameState.SELECTING_TUBE) }
            }
        } else if (state.selectedTubeId == tubeId) {
            // Deselect
            soundManager.playSelect()
            _uiState.update { it.copy(selectedTubeId = null, gameState = GameState.IDLE) }
        } else {
            // Try to pour
            val sourceId = state.selectedTubeId
            val destId = tubeId
            attemptPour(sourceId, destId)
        }
    }

    private fun attemptPour(sourceId: Int, destId: Int) {
        val state = _uiState.value
        val level = state.level ?: return
        val sourceTube = level.tubes.find { it.id == sourceId } ?: return
        val destTube = level.tubes.find { it.id == destId } ?: return

        val topColor = sourceTube.topColor ?: return
        val amount = sourceTube.topColorCount()
        
        val canPour = destTube.isEmpty || (destTube.topColor == topColor && !destTube.isFull)
        
        if (canPour) {
            val spaceAvailable = destTube.capacity - destTube.colors.size
            val amountToPour = minOf(amount, spaceAvailable)
            
            if (amountToPour > 0) {
                _uiState.update { 
                    it.copy(
                        undoHistory = (it.undoHistory + level).takeLast(50),
                        gameState = GameState.POURING,
                        animatingSource = sourceId,
                        animatingDest = destId,
                        selectedTubeId = null
                    ) 
                }
                
                // Perform actual pour after a short delay (animation mock)
                viewModelScope.launch {
                    delay(300) // Pour animation duration
                    soundManager.playDrop()
                    executePour(sourceTube, destTube, topColor, amountToPour)
                }
            } else {
                // Invalid - dest is full
                soundManager.playError()
                _uiState.update { it.copy(selectedTubeId = null, gameState = GameState.IDLE) }
            }
        } else {
            // Invalid pour
            soundManager.playError()
            _uiState.update { it.copy(selectedTubeId = null, gameState = GameState.IDLE) }
        }
    }

    private fun executePour(sourceTube: Tube, destTube: Tube, color: LiquidColor, amountToPour: Int) {
        val state = _uiState.value
        val level = state.level ?: return
        
        val newSourceColors = sourceTube.colors.dropLast(amountToPour)
        val newDestColors = destTube.colors + List(amountToPour) { color }
        
        val newTubes = level.tubes.map {
            when (it.id) {
                sourceTube.id -> it.copy(colors = newSourceColors)
                destTube.id -> it.copy(colors = newDestColors)
                else -> it
            }
        }
        
        val newMove = Move(sourceTube.id, destTube.id, color, amountToPour)
        
        val newLevel = level.copy(tubes = newTubes)
        val isCompleted = newLevel.tubes.all { it.isComplete || it.isEmpty }
        
        val hasLegalMove = hasLegalMove(newLevel)
        val earnedStars = if (isCompleted) calculateStars(state.moves + 1, state.timeSeconds, state.hintsAvailable < 5, state.undosAvailable < 5) else 0
        _uiState.update {
            it.copy(
                level = newLevel,
                moves = it.moves + 1,
                moveHistory = it.moveHistory + newMove,
                gameState = when { isCompleted -> GameState.COMPLETED; !hasLegalMove -> GameState.FAILED; else -> GameState.IDLE },
                animatingSource = null,
                animatingDest = null,
                earnedStars = earnedStars,
                hintSourceId = null,
                hintDestinationId = null,
                hintMessage = null
            )
        }
        saveCloudSession("playing")
        
        if (!isCompleted && hasLegalMove) {
            saveActiveGameState()
        }
        
        if (isCompleted) {
            soundManager.playComplete()
            saveCloudSession("won")
            handleLevelCompletion(newLevel.id)
        } else if (!hasLegalMove) {
            timerJob?.cancel()
            saveCloudSession("lost")
            viewModelScope.launch {
                val stats = playerStats.value
                repository.updateStats(stats.copy(lives = (stats.lives - 1).coerceAtLeast(0)))
            }
        }
    }

    fun undoMove() {
        val state = _uiState.value
        if (state.gameState != GameState.IDLE && state.gameState != GameState.SELECTING_TUBE) return
        if (state.undosAvailable <= 0 || state.undoHistory.isEmpty()) return
        val previousLevel = state.undoHistory.last()
        
        soundManager.playSelect()
        
        _uiState.update {
            it.copy(
                level = previousLevel,
                undoHistory = it.undoHistory.dropLast(1),
                moveHistory = it.moveHistory.dropLast(1),
                moves = (it.moves - 1).coerceAtLeast(0),
                undosAvailable = it.undosAvailable - 1,
                gameState = GameState.IDLE,
                selectedTubeId = null,
                hintSourceId = null, hintDestinationId = null, hintMessage = null
            )
        }
        
        // Update persistent stats
        viewModelScope.launch {
            val stats = playerStats.value
            repository.updateStats(stats.copy(undos = stats.undos - 1))
        }
    }

    fun pauseGame() {
        if (_uiState.value.gameState == GameState.IDLE || _uiState.value.gameState == GameState.SELECTING_TUBE) {
            _uiState.update { it.copy(gameState = GameState.PAUSED) }
            timerJob?.cancel()
            saveCloudSession("paused")
            saveActiveGameState()
        }
    }

    fun resumeGame() {
        if (_uiState.value.gameState == GameState.PAUSED) {
            _uiState.update { it.copy(gameState = GameState.IDLE) }
            startTimer()
            saveCloudSession("playing")
        }
    }

    fun showHint() {
        val state = _uiState.value
        if (state.gameState != GameState.IDLE || state.hintsAvailable <= 0) return
        val level = state.level ?: return
        val move = findUsefulMove(level) ?: run {
            _uiState.update { it.copy(hintMessage = "No useful move available.") }
            return
        }
        _uiState.update { it.copy(hintsAvailable = it.hintsAvailable - 1, hintSourceId = move.first, hintDestinationId = move.second, hintMessage = "Try pouring Tube ${move.first + 1} -> Tube ${move.second + 1}") }
        
        // Update persistent stats
        viewModelScope.launch {
            val stats = playerStats.value
            repository.updateStats(stats.copy(hints = stats.hints - 1))
        }
    }

    fun useExtraTube() {
        val state = _uiState.value
        if (state.extraTubesAvailable <= 0) return
        val level = state.level ?: return
        val nextTubeId = level.tubes.maxOfOrNull { it.id }?.plus(1) ?: 0
        val newTotal = state.extraTubesAvailable - 1
        _uiState.update {
            it.copy(
                level = level.copy(tubes = level.tubes + Tube(nextTubeId, level.tubes.firstOrNull()?.capacity ?: 4, emptyList())),
                extraTubesAvailable = newTotal
            )
        }
        // Only consume from persistent inventory once the 2 free tubes are used up.
        viewModelScope.launch {
            val stats = playerStats.value
            val ownedBefore = stats.extraTubes
            if (newTotal < ownedBefore) {
                repository.updateStats(stats.copy(extraTubes = newTotal))
            }
        }
    }

    fun clearHint() { _uiState.update { it.copy(hintSourceId = null, hintDestinationId = null, hintMessage = null) } }

    private fun findUsefulMove(level: Level): Pair<Int, Int>? {
        val candidates = level.tubes.flatMap { source -> level.tubes.filter { it.id != source.id }.mapNotNull { destination ->
            val color = source.topColor
            val amount = source.topColorCount()
            if (color != null && !destination.isFull && (destination.isEmpty || destination.topColor == color)) {
                val improves = destination.isEmpty || destination.topColor == color
                if (improves) source.id to destination.id else null
            } else null
        } }
        return candidates.firstOrNull()
    }

    private fun hasLegalMove(level: Level): Boolean = findUsefulMove(level) != null

    private fun calculateStars(moves: Int, seconds: Int, usedHint: Boolean, usedUndo: Boolean): Int {
        val level = _uiState.value.level ?: return 1
        val movePenalty = (moves - (level.tubes.size * 2)).coerceAtLeast(0)
        val timePenalty = (seconds / 30).coerceAtMost(4)
        return (6 - movePenalty / 3 - timePenalty - if (usedHint) 1 else 0 - if (usedUndo) 1 else 0).coerceIn(1, 6)
    }

    // Compute updated stats after a level is won (unlock next level, track speed solves, auto-unlock level themes).
    private fun rewardStatsForLevelCompletion(stats: PlayerStats, levelId: Int, timeSeconds: Int): PlayerStats {
        val newHighest = maxOf(stats.highestUnlockedLevel, levelId + 1)
        val newSpeed = if (timeSeconds <= 30) stats.speedSolves + 1 else stats.speedSolves
        val unlocked = stats.unlockedThemes.split(",").toMutableList()
        ThemeRepository.themes.forEach { theme ->
            if (theme.unlockType == UnlockType.LEVEL && !unlocked.contains(theme.id) && newHighest >= theme.unlockValue) {
                unlocked.add(theme.id)
            }
        }
        val newUnlocked = if (unlocked.isEmpty()) "classic" else unlocked.joinToString(",")
        return stats.copy(
            highestUnlockedLevel = newHighest,
            speedSolves = newSpeed,
            unlockedThemes = newUnlocked
        )
    }

    private fun handleLevelCompletion(levelId: Int) {
        timerJob?.cancel()
        autoSaveJob?.cancel()
        clearSavedGame()
        val state = _uiState.value
        viewModelScope.launch {
            val stats = playerStats.value
            
            if (state.gameMode == GameMode.DAILY_CHALLENGE) {
                val date = state.dailyDate ?: return@launch
                val existingRecord = repository.getDailyRecord(date).firstOrNull()
                
                val score = maxOf(10, 1000 - state.moves * 10 - state.timeSeconds * 2)
                
                if (existingRecord == null || !existingRecord.isCompleted) {
                    val newRecord = DailyRecord(
                        dateString = date,
                        score = score,
                        moves = state.moves,
                        timeSeconds = state.timeSeconds,
                        isCompleted = true,
                        rewardClaimed = true // Claim immediately for simplicity
                    )
                    
                    val newStreak = if (stats.lastPlayedDate != date) stats.dailyStreak + 1 else stats.dailyStreak
                    repository.updateStats(
                        stats.copy(
                            coins = stats.coins + 100,
                            xp = stats.xp + 250,
                            dailyStreak = newStreak,
                            longestStreak = maxOf(stats.longestStreak, newStreak),
                            lastPlayedDate = date
                        )
                    )
                    repository.insertDailyRecord(newRecord)
                }
            } else {
                // Persist locally FIRST so progress survives a restart regardless of network/Firebase.
                val localStars = state.earnedStars.coerceAtLeast(1)
                val localCoins = 25
                val localXp = 50
                _uiState.update { it.copy(earnedStars = localStars, rewardCoins = localCoins, rewardXp = localXp) }
                repository.saveLevelProgress(LevelProgress(levelId, localStars, true, state.moves, state.timeSeconds))
                val rewarded = rewardStatsForLevelCompletion(stats, levelId, state.timeSeconds)
                repository.updateStats(rewarded.copy(coins = rewarded.coins + localCoins, xp = rewarded.xp + localXp))

                // Then sync to Firestore (best-effort, does not affect local save).
                firestoreRepository?.completeLevel(
                    levelId = levelId,
                    moves = state.moves,
                    elapsedTime = state.timeSeconds,
                    hintsUsed = 5 - state.hintsAvailable,
                    undosUsed = 5 - state.undosAvailable
                )?.addOnSuccessListener { result ->
                    val data = result.data as? Map<*, *> ?: return@addOnSuccessListener
                    val stars = (data["stars"] as? Number)?.toInt() ?: localStars
                    val rewardCoins = (data["rewardCoins"] as? Number)?.toInt() ?: 0
                    val rewardXp = (data["rewardXp"] as? Number)?.toInt() ?: 0
                    _uiState.update { it.copy(earnedStars = stars, rewardCoins = rewardCoins, rewardXp = rewardXp) }
                    viewModelScope.launch {
                        repository.saveLevelProgress(LevelProgress(levelId, stars, true, state.moves, state.timeSeconds))
                        val rewarded = rewardStatsForLevelCompletion(stats, levelId, state.timeSeconds)
                        repository.updateStats(rewarded.copy(coins = rewarded.coins + rewardCoins, xp = rewarded.xp + rewardXp))
                    }
                }
            }
        }
    }

    fun restartLevel() {
        soundManager.playSelect()
        clearSavedGame()
        val state = _uiState.value
        if (state.gameMode == GameMode.DAILY_CHALLENGE) {
            state.dailyDate?.let { loadDailyChallenge(it) }
        } else {
            state.level?.id?.let { loadLevel(it) }
        }
    }

    private fun startCloudSession(levelId: Int) {
        sessionId = UUID.randomUUID().toString()
        saveCloudSession("playing", levelId)
    }

    private fun saveCloudSession(status: String, levelId: Int? = null) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val id = sessionId ?: return
        val actualLevelId = levelId ?: _uiState.value.level?.id ?: return
        firestoreRepository?.saveGameSession(uid, id, mapOf(
            "sessionId" to id,
            "uid" to uid,
            "levelId" to actualLevelId,
            "moves" to _uiState.value.moves,
            "elapsedTime" to _uiState.value.timeSeconds,
            "status" to status,
            "updatedAt" to FieldValue.serverTimestamp()
        ))
    }

    fun addCoins(amount: Int) {
        viewModelScope.launch {
            val stats = playerStats.value
            repository.updateStats(stats.copy(coins = stats.coins + amount))
        }
    }

    fun addHint(amount: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(hintsAvailable = it.hintsAvailable + amount) }
            val stats = playerStats.value
            repository.updateStats(stats.copy(hints = stats.hints + amount))
        }
    }

    fun addUndo(amount: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(undosAvailable = it.undosAvailable + amount) }
            val stats = playerStats.value
            repository.updateStats(stats.copy(undos = stats.undos + amount))
        }
    }

    fun addLife(amount: Int) {
        viewModelScope.launch {
            val stats = playerStats.value
            val newLives = (stats.lives + amount).coerceAtMost(5)
            repository.updateStats(stats.copy(lives = newLives))
        }
    }

    fun buyLives(amount: Int, cost: Int) {
        viewModelScope.launch {
            val stats = playerStats.value
            if (stats.coins >= cost) {
                repository.updateStats(stats.copy(
                    coins = stats.coins - cost,
                    lives = stats.lives + amount
                ))
            }
        }
    }

    private fun todayString(): String = java.time.LocalDate.now().toString()

    fun buyHints(amount: Int, cost: Int): Boolean {
        var ok = false
        viewModelScope.launch {
            val stats = playerStats.value
            if (stats.coins >= cost) {
                repository.updateStats(stats.copy(coins = stats.coins - cost, hints = stats.hints + amount))
                ok = true
            }
        }
        return ok
    }

    fun buyUndos(amount: Int, cost: Int): Boolean {
        var ok = false
        viewModelScope.launch {
            val stats = playerStats.value
            if (stats.coins >= cost) {
                repository.updateStats(stats.copy(coins = stats.coins - cost, undos = stats.undos + amount))
                ok = true
            }
        }
        return ok
    }

    fun buyExtraTubes(amount: Int, cost: Int): Boolean {
        var ok = false
        viewModelScope.launch {
            val stats = playerStats.value
            if (stats.coins >= cost) {
                repository.updateStats(stats.copy(coins = stats.coins - cost, extraTubes = stats.extraTubes + amount))
                ok = true
            }
        }
        return ok
    }

    // Daily free coins: claimable once per calendar day.
    fun claimDailyFreeCoins(amount: Int): Boolean {
        val today = todayString()
        val stats = playerStats.value
        if (stats.lastDailyCoinsClaim == today) return false
        viewModelScope.launch {
            repository.updateStats(stats.copy(coins = stats.coins + amount, lastDailyCoinsClaim = today))
        }
        return true
    }

    fun hasClaimedDailyFreeCoins(): Boolean = playerStats.value.lastDailyCoinsClaim == todayString()

    // Achievements: claim once per achievement id.
    fun claimAchievement(id: String, reward: Int): Boolean {
        val stats = playerStats.value
        if (stats.claimedAchievements.split(",").contains(id)) return false
        viewModelScope.launch {
            val claimed = stats.claimedAchievements.split(",").toMutableList().apply { add(id) }
            repository.updateStats(stats.copy(coins = stats.coins + reward, claimedAchievements = claimed.joinToString(",")))
        }
        return true
    }

    fun isAchievementClaimed(id: String): Boolean =
        playerStats.value.claimedAchievements.split(",").contains(id)

    // Streak milestones: claim once per milestone day count.
    fun claimStreakMilestone(days: Int, reward: Int): Boolean {
        val stats = playerStats.value
        if (stats.claimedStreakMilestones.split(",").contains(days.toString())) return false
        if (stats.dailyStreak < days) return false
        viewModelScope.launch {
            val claimed = stats.claimedStreakMilestones.split(",").toMutableList().apply { add(days.toString()) }
            repository.updateStats(stats.copy(coins = stats.coins + reward, claimedStreakMilestones = claimed.joinToString(",")))
        }
        return true
    }

    fun isStreakMilestoneClaimed(days: Int): Boolean =
        playerStats.value.claimedStreakMilestones.split(",").contains(days.toString())

    // ---------- Friends (stored locally as JSON in player_stats) ----------

    fun getFriends(): List<Friend> {
        val json = playerStats.value.friendsJson
        if (json.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<Friend>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                list.add(
                    Friend(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        level = o.optInt("level", 1),
                        online = o.optBoolean("online", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun searchFriends(query: String): List<Friend> {
        val q = query.trim()
        val all = getFriends()
        if (q.isBlank()) return all
        return all.filter { it.name.contains(q, ignoreCase = true) }
    }

    private fun saveFriends(list: List<Friend>) {
        viewModelScope.launch {
            val arr = JSONArray()
            list.forEach { f ->
                val o = JSONObject()
                o.put("id", f.id)
                o.put("name", f.name)
                o.put("level", f.level)
                o.put("online", f.online)
                arr.put(o)
            }
            val stats = playerStats.value
            repository.updateStats(stats.copy(friendsJson = arr.toString()))
        }
    }

    fun addFriend(name: String): Boolean {
        val clean = name.trim()
        if (clean.isBlank()) return false
        val list = getFriends().toMutableList()
        if (list.any { it.name.equals(clean, ignoreCase = true) }) return false
        list.add(
            Friend(
                id = UUID.randomUUID().toString(),
                name = clean,
                level = Random.nextInt(1, 61),
                online = Random.nextBoolean()
            )
        )
        saveFriends(list)
        return true
    }

    fun removeFriend(id: String) {
        saveFriends(getFriends().filter { it.id != id })
    }

    // ---------- Events (progress derived from real stats) ----------

    fun getEvents(): List<GameEvent> {
        val stats = playerStats.value
        val claimed = stats.claimedEvents.split(",").toSet()
        val unlockedThemes = stats.unlockedThemes.split(",").count { it.isNotBlank() && it != "classic" }
        return listOf(
            GameEvent("marathon", "Level Marathon", "Complete 20 levels", 20, (stats.highestUnlockedLevel - 1).coerceAtLeast(0), 300, claimed.contains("marathon")),
            GameEvent("streak5", "Daily Devotee", "Reach a 5-day streak", 5, stats.dailyStreak, 200, claimed.contains("streak5")),
            GameEvent("streak10", "Streak Legend", "Reach a 10-day streak", 10, stats.longestStreak, 400, claimed.contains("streak10")),
            GameEvent("themes", "Theme Collector", "Unlock 3 themes", 3, unlockedThemes, 150, claimed.contains("themes")),
            GameEvent("coins", "Coin Hoarder", "Hold 2000 coins", 2000, stats.coins, 250, claimed.contains("coins"))
        )
    }

    fun claimEvent(event: GameEvent): Boolean {
        if (event.claimed || event.current < event.goal) return false
        val stats = playerStats.value
        if (stats.claimedEvents.split(",").contains(event.id)) return false
        viewModelScope.launch {
            val claimed = stats.claimedEvents.split(",").toMutableList().apply { add(event.id) }
            repository.updateStats(
                stats.copy(coins = stats.coins + event.reward, claimedEvents = claimed.joinToString(","))
            )
        }
        return true
    }
}

data class Friend(
    val id: String,
    val name: String,
    val level: Int,
    val online: Boolean
)

data class GameEvent(
    val id: String,
    val title: String,
    val description: String,
    val goal: Int,
    val current: Int,
    val reward: Int,
    val claimed: Boolean
) {
    val progress: Float get() = if (goal <= 0) 1f else (current.coerceAtMost(goal).toFloat() / goal)
    val isComplete: Boolean get() = current >= goal
}