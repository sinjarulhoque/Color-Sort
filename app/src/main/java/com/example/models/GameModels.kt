package com.example.models

import androidx.compose.ui.graphics.Color

enum class LiquidColor(val color: Color) {
    RED(Color(0xFFE53935)),
    BLUE(Color(0xFF1E88E5)),
    GREEN(Color(0xFF43A047)),
    YELLOW(Color(0xFFFDD835)),
    ORANGE(Color(0xFFFB8C00)),
    PURPLE(Color(0xFF8E24AA)),
    PINK(Color(0xFFD81B60)),
    CYAN(Color(0xFF00ACC1))
}

data class Tube(
    val id: Int,
    val capacity: Int,
    val colors: List<LiquidColor> // Bottom to top
) {
    val isFull: Boolean get() = colors.size == capacity
    val isEmpty: Boolean get() = colors.isEmpty()
    val isComplete: Boolean get() = isFull && colors.distinct().size == 1

    val topColor: LiquidColor? get() = colors.lastOrNull()
    
    // Returns the number of continuous same-color layers at the top
    fun topColorCount(): Int {
        if (colors.isEmpty()) return 0
        val top = topColor
        var count = 0
        for (i in colors.indices.reversed()) {
            if (colors[i] == top) count++ else break
        }
        return count
    }
}

data class Level(
    val id: Int,
    val tubes: List<Tube>,
    val difficulty: Int = 1
)

enum class GameState {
    IDLE,
    SELECTING_TUBE,
    POURING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class Move(
    val sourceTubeId: Int,
    val destinationTubeId: Int,
    val color: LiquidColor,
    val amount: Int
)
