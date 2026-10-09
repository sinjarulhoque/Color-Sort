package com.example

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.FirestoreRepository
import com.example.utils.SoundManager
import com.example.utils.MusicManager
import com.example.ads.AdService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class ColorSortApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: GameRepository
    lateinit var soundManager: SoundManager
    lateinit var firestoreRepository: FirestoreRepository
    lateinit var adService: AdService
    lateinit var musicManager: MusicManager

    override fun onCreate() {
        super.onCreate()
        
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new active_game_state table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS active_game_state (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        levelId INTEGER NOT NULL,
                        dailyDate TEXT,
                        tubesJson TEXT NOT NULL,
                        moves INTEGER NOT NULL,
                        timeSeconds INTEGER NOT NULL,
                        hintsAvailable INTEGER NOT NULL,
                        undosAvailable INTEGER NOT NULL,
                        extraTubesAvailable INTEGER NOT NULL,
                        selectedThemeId TEXT NOT NULL,
                        undoHistoryJson TEXT NOT NULL,
                        lastSaved INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE player_stats ADD COLUMN longestStreak INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN extraTubes INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN speedSolves INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN lastDailyCoinsClaim TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN claimedAchievements TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN claimedStreakMilestones TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE player_stats ADD COLUMN friendsJson TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN claimedEvents TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE player_stats ADD COLUMN avatarIndex INTEGER NOT NULL DEFAULT -1")
            }
        }
        
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "color_sort_db"
        ).addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
         .fallbackToDestructiveMigration() // Fallback for other versions
         .build()
        repository = GameRepository(database.gameDao())
        soundManager = SoundManager(this)
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestoreRepository = FirestoreRepository(firestore)
        adService = AdService(this)
        adService.initialize()
        musicManager = MusicManager()
    }
}
