package com.example.data

import com.example.models.Level
import com.example.models.Tube
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val dao: GameDao) {
    private val gson = Gson()
    
    val levelProgress: Flow<List<LevelProgress>> = dao.getAllLevelProgress()
    
    val playerStats: Flow<PlayerStats> = dao.getPlayerStats().map { it ?: PlayerStats() }

    fun getDailyRecord(date: String): Flow<DailyRecord?> = dao.getDailyRecord(date)

    fun getAllDailyRecords(): Flow<List<DailyRecord>> = dao.getAllDailyRecords()

    suspend fun saveLevelProgress(progress: LevelProgress) {
        dao.insertLevelProgress(progress)
    }

    suspend fun updateStats(stats: PlayerStats) {
        dao.updateStats(stats)
    }

    suspend fun insertDailyRecord(record: DailyRecord) {
        dao.insertDailyRecord(record)
    }

    // Active game state (mid-level progress)
    fun getActiveGameState(): Flow<ActiveGameState?> = dao.getActiveGameState()

    suspend fun saveActiveGameState(state: ActiveGameState) {
        dao.saveActiveGameState(state)
    }

    suspend fun clearActiveGameState() {
        dao.clearActiveGameState()
    }

    // Helper to serialize tubes for storage
    fun serializeTubes(tubes: List<Tube>): String = gson.toJson(tubes)
    fun deserializeTubes(json: String): List<Tube> = gson.fromJson(json, object : TypeToken<List<Tube>>() {}.type)
    fun serializeLevels(levels: List<Level>): String = gson.toJson(levels)
    fun deserializeLevels(json: String): List<Level> = gson.fromJson(json, object : TypeToken<List<Level>>() {}.type)
}
