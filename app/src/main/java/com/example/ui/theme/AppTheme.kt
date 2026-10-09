package com.example.ui.theme

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.Calendar

data class AppColors(
    val background: Color,
    val backgroundGradientStart: Color,
    val backgroundGradientEnd: Color,
    val surface: Color,
    val card: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val border: Color,
    val accent: Color,
    val success: Color,
    val warning: Color,
    val error: Color
)

val LightColors = AppColors(
    background = Color(0xFFF4F6FB),
    backgroundGradientStart = Color(0xFFEEF2FB),
    backgroundGradientEnd = Color(0xFFDCE6F7),
    surface = Color(0xFFFFFFFF),
    card = Color(0xFFE9EEF6),
    primaryText = Color(0xFF0F172A),
    secondaryText = Color(0xFF64748B),
    border = Color(0xFFCBD5E1),
    accent = Color(0xFF3B82F6),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    error = Color(0xFFEF4444)
)

val DarkColors = AppColors(
    background = Color(0xFF0F172A),
    backgroundGradientStart = Color(0xFF0F172A),
    backgroundGradientEnd = Color(0xFF1E1B4B),
    surface = Color(0xFF1E293B),
    card = Color(0xFF1E293B),
    primaryText = Color.White,
    secondaryText = Color.Gray,
    border = Color.White.copy(alpha = 0.1f),
    accent = Color(0xFF3B82F6),
    success = Color(0xFF10B981),
    warning = Color(0xFFFBBF24),
    error = Color(0xFFEF4444)
)

val LocalAppColors = compositionLocalOf { DarkColors }
val LocalDarkMode = compositionLocalOf<MutableState<Boolean>> { mutableStateOf(true) }

object AppPrefs {
    private const val NAME = "app_prefs"
    private const val KEY_DARK = "dark_mode"
    private const val KEY_DAILY = "last_daily_reward"
    private const val KEY_DAILY_DAYS = "daily_claimed_days"
    private const val KEY_FREE = "last_free_coins"
    private const val KEY_MUSIC = "music_enabled"

    const val FREE_COINS_INTERVAL_MS = 2 * 60 * 60 * 1000L // 2 hours

    private fun prefs(context: Context) = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isDark(context: Context) = prefs(context).getBoolean(KEY_DARK, true)
    fun setDark(context: Context, dark: Boolean) = prefs(context).edit().putBoolean(KEY_DARK, dark).apply()

    fun isMusic(context: Context) = prefs(context).getBoolean(KEY_MUSIC, true)
    fun setMusic(context: Context, enabled: Boolean) = prefs(context).edit().putBoolean(KEY_MUSIC, enabled).apply()

    fun getLastDailyReward(context: Context) = prefs(context).getLong(KEY_DAILY, 0L)
    fun setLastDailyReward(context: Context, t: Long) = prefs(context).edit().putLong(KEY_DAILY, t).apply()

    fun getDailyClaimedDays(context: Context): Set<Int> {
        return prefs(context).getString(KEY_DAILY_DAYS, "")?.split(",")?.filter { it.isNotBlank() }?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }
    fun addDailyClaimedDay(context: Context, day: Int) {
        val set = getDailyClaimedDays(context).toMutableSet().apply { add(day) }
        prefs(context).edit().putString(KEY_DAILY_DAYS, set.joinToString(",")).apply()
    }
    // Day available to claim today (0 = none / already claimed today). Resets next day.
    fun dailyAvailableDay(context: Context): Int {
        if (isSameDay(getLastDailyReward(context))) return 0
        val claimed = getDailyClaimedDays(context)
        val next = (claimed.maxOrNull() ?: 0) + 1
        return if (next > 7) 0 else next
    }

    fun getLastFreeCoins(context: Context) = prefs(context).getLong(KEY_FREE, 0L)
    fun setLastFreeCoins(context: Context, t: Long) = prefs(context).edit().putLong(KEY_FREE, t).apply()

    fun nextMidnight(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun isSameDay(t: Long): Boolean {
        if (t == 0L) return false
        val a = Calendar.getInstance().apply { timeInMillis = t }
        val b = Calendar.getInstance()
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }

    // Target epoch millis for the DAILY REWARD countdown (0 = ready now)
    fun dailyRewardTarget(context: Context): Long {
        val last = getLastDailyReward(context)
        return if (isSameDay(last)) nextMidnight() else 0L
    }

    // Target epoch millis for the FREE COINS countdown (0 = ready now)
    fun freeCoinsTarget(context: Context): Long {
        val last = getLastFreeCoins(context)
        val target = last + FREE_COINS_INTERVAL_MS
        return if (target > System.currentTimeMillis()) target else 0L
    }
}

fun formatHMS(ms: Long): String {
    val totalSec = (ms / 1000).coerceAtLeast(0).toInt()
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return "%d:%02d:%02d".format(h, m, s)
}

@Composable
fun rememberCountdown(targetEpochMillis: Long): String {
    val remaining = remember(targetEpochMillis) {
        mutableStateOf((targetEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L))
    }
    // Tick every second while there is time remaining.
    androidx.compose.runtime.LaunchedEffect(targetEpochMillis) {
        remaining.value = (targetEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        while (remaining.value > 0) {
            kotlinx.coroutines.delay(1000)
            remaining.value = (targetEpochMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        }
    }
    return formatHMS(remaining.value)
}

@Composable
fun MinimalBackground(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Box(modifier.fillMaxSize().background(
        Brush.verticalGradient(listOf(colors.backgroundGradientStart, colors.backgroundGradientEnd))
    )) {
        // Subtle minimalist decorative circles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            colors.accent.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.2f, 0.15f),
                        radius = 0.9f
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            colors.success.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.85f, 0.9f),
                        radius = 0.8f
                    )
                )
        )
    }
}
