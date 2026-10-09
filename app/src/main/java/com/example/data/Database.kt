package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.models.Level
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "level_progress")
data class LevelProgress(
    @PrimaryKey val levelId: Int,
    val stars: Int,
    val isCompleted: Boolean = false,
    val bestMoves: Int = -1,
    val bestTimeSeconds: Int = -1
)

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey val dateString: String,
    val score: Int = 0,
    val moves: Int = 0,
    val timeSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val rewardClaimed: Boolean = false
)

@Entity(tableName = "player_stats")
data class PlayerStats(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 0,
    val lives: Int = 5,
    val xp: Int = 0,
    val nextLifeRegenTimeMillis: Long = 0L,
    val highestUnlockedLevel: Int = 1,
    val totalStars: Int = 0,
    val selectedThemeId: String = "classic",
    val unlockedThemes: String = "classic",
    val dailyStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastPlayedDate: String = "",
    val hints: Int = 5,
    val undos: Int = 5,
    val extraTubes: Int = 0,
    val speedSolves: Int = 0,
    val lastDailyCoinsClaim: String = "",
    val claimedAchievements: String = "",
    val claimedStreakMilestones: String = "",
    val friendsJson: String = "",
    val claimedEvents: String = "",
    val avatarIndex: Int = -1
)

// New entity for saving active game state (mid-level progress)
@Entity(tableName = "active_game_state")
data class ActiveGameState(
    @PrimaryKey val id: Int = 1, // Single active game
    val levelId: Int,
    val dailyDate: String?,
    val tubesJson: String, // Serialized tube list (JSON string)
    val moves: Int,
    val timeSeconds: Int,
    val hintsAvailable: Int,
    val undosAvailable: Int,
    val extraTubesAvailable: Int,
    val selectedThemeId: String,
    val undoHistoryJson: String, // Serialized undo history (JSON string)
    val lastSaved: Long = System.currentTimeMillis()
)

@Dao
interface GameDao {
    @Query("SELECT * FROM level_progress")
    fun getAllLevelProgress(): Flow<List<LevelProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelProgress(progress: LevelProgress)

    @Query("SELECT * FROM player_stats WHERE id = 1")
    fun getPlayerStats(): Flow<PlayerStats?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: PlayerStats)

    @Query("SELECT * FROM daily_records WHERE dateString = :date")
    fun getDailyRecord(date: String): Flow<DailyRecord?>

    @Query("SELECT * FROM daily_records")
    fun getAllDailyRecords(): Flow<List<DailyRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyRecord(record: DailyRecord)

    // Active game state methods
    @Query("SELECT * FROM active_game_state WHERE id = 1")
    fun getActiveGameState(): Flow<ActiveGameState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveGameState(state: ActiveGameState)

    @Query("DELETE FROM active_game_state WHERE id = 1")
    suspend fun clearActiveGameState()
}

@Database(entities = [LevelProgress::class, PlayerStats::class, DailyRecord::class, ActiveGameState::class], version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
