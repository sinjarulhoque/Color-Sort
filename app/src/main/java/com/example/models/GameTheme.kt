package com.example.models

import androidx.compose.ui.graphics.Color
import com.example.R

enum class UnlockType { FREE, COINS, LEVEL }

data class GameTheme(
    val id: String,
    val name: String,
    val backgroundAsset: Int?,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val textColor: Color,
    val cardColor: Color,
    val buttonColor: Color,
    val unlockType: UnlockType,
    val unlockValue: Int,
    val isDefault: Boolean = false
)

object ThemeRepository {
    val themes = listOf(
        GameTheme(
            id = "classic",
            name = "Classic",
            backgroundAsset = null,
            primaryColor = Color(0xFF1E3A8A), // Dark blue
            secondaryColor = Color(0xFF0F172A),
            accentColor = Color(0xFF3B82F6),
            textColor = Color.White,
            cardColor = Color(0xFF1E293B),
            buttonColor = Color(0xFF3B82F6),
            unlockType = UnlockType.FREE,
            unlockValue = 0,
            isDefault = true
        ),
        GameTheme(
            id = "beach",
            name = "Beach",
            backgroundAsset = R.drawable.theme_beach,
            primaryColor = Color(0xFF0EA5E9), // Aqua
            secondaryColor = Color(0xFF38BDF8),
            accentColor = Color(0xFFFBBF24), // Sun
            textColor = Color.White,
            cardColor = Color.Black.copy(alpha = 0.5f),
            buttonColor = Color(0xFF0EA5E9),
            unlockType = UnlockType.COINS,
            unlockValue = 500
        ),
        GameTheme(
            id = "night",
            name = "Night",
            backgroundAsset = R.drawable.theme_night,
            primaryColor = Color(0xFF8B5CF6), // Purple
            secondaryColor = Color(0xFF1E1B4B),
            accentColor = Color(0xFF6366F1), // Neon Blue
            textColor = Color.White,
            cardColor = Color.Black.copy(alpha = 0.5f),
            buttonColor = Color(0xFF8B5CF6),
            unlockType = UnlockType.LEVEL,
            unlockValue = 10
        ),
        GameTheme(
            id = "forest",
            name = "Forest",
            backgroundAsset = R.drawable.theme_forest,
            primaryColor = Color(0xFF22C55E), // Green
            secondaryColor = Color(0xFF14532D),
            accentColor = Color(0xFF84CC16), // Light green
            textColor = Color.White,
            cardColor = Color.Black.copy(alpha = 0.5f),
            buttonColor = Color(0xFF22C55E),
            unlockType = UnlockType.LEVEL,
            unlockValue = 20
        ),
        GameTheme(
            id = "candy",
            name = "Candy",
            backgroundAsset = R.drawable.theme_candy,
            primaryColor = Color(0xFFEC4899), // Pink
            secondaryColor = Color(0xFF831843),
            accentColor = Color(0xFFD946EF), // Purple
            textColor = Color.White,
            cardColor = Color.Black.copy(alpha = 0.5f),
            buttonColor = Color(0xFFEC4899),
            unlockType = UnlockType.COINS,
            unlockValue = 1500
        ),
        GameTheme(
            id = "space",
            name = "Space",
            backgroundAsset = R.drawable.theme_space,
            primaryColor = Color(0xFF4F46E5), // Deep Blue
            secondaryColor = Color(0xFF020617),
            accentColor = Color(0xFFA855F7), // Neon purple
            textColor = Color.White,
            cardColor = Color.Black.copy(alpha = 0.5f),
            buttonColor = Color(0xFF4F46E5),
            unlockType = UnlockType.LEVEL,
            unlockValue = 50
        )
    )

    fun getThemeById(id: String): GameTheme {
        return themes.find { it.id == id } ?: themes.first()
    }
}
