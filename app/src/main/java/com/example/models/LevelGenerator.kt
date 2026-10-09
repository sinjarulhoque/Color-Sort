package com.example.models

object LevelGenerator {
    fun getLevel(id: Int): Level {
        return generateRandomLevel(id)
    }

    fun getDailyLevel(dateString: String): Level {
        val seed = dateString.hashCode().toLong()
        val random = java.util.Random(seed)
        
        // Fixed hard difficulty for daily
        val numColors = minOf(6, LiquidColor.values().size)
        val emptyTubes = 2
        val capacity = 4
        
        val allColors = LiquidColor.values().take(numColors)
        val colorPool = mutableListOf<LiquidColor>()
        allColors.forEach { color ->
            repeat(capacity) { colorPool.add(color) }
        }
        
        // Deterministic shuffle
        colorPool.shuffle(random)
        
        val tubes = mutableListOf<Tube>()
        for (i in 0 until numColors) {
            val tubeColors = colorPool.subList(i * capacity, (i + 1) * capacity)
            tubes.add(Tube(i, capacity, tubeColors.toList()))
        }
        for (i in 0 until emptyTubes) {
            tubes.add(Tube(numColors + i, capacity, emptyList()))
        }
        
        return Level(id = 9999, tubes = tubes, difficulty = 5)
    }

    private fun generateRandomLevel(id: Int): Level {
        // Dynamic scaling based on level id to "automatically change the 3-4-5"
        // Level 1-2: 2 colors (3-4 tubes)
        // Level 3-4: 3 colors (5 tubes)
        // Level 5-6: 4 colors (6 tubes)
        // Level 7-8: 5 colors (7 tubes)
        val numColors = minOf(2 + (id - 1) / 2, LiquidColor.values().size)
        val emptyTubes = if (id < 3) 1 else 2
        val capacity = 4
        
        val allColors = LiquidColor.values().take(numColors)
        val colorPool = mutableListOf<LiquidColor>()
        allColors.forEach { color ->
            repeat(capacity) { colorPool.add(color) }
        }
        colorPool.shuffle()
        
        val tubes = mutableListOf<Tube>()
        for (i in 0 until numColors) {
            val tubeColors = colorPool.subList(i * capacity, (i + 1) * capacity)
            tubes.add(Tube(i, capacity, tubeColors.toList()))
        }
        for (i in 0 until emptyTubes) {
            tubes.add(Tube(numColors + i, capacity, emptyList()))
        }
        
        return Level(id = id, tubes = tubes, difficulty = id)
    }
}
