package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.ArrowPinStyle
import com.example.model.BoardBackgroundStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sandras_madness_prefs")

data class PlayerProgressState(
    val highestUnlockedLevel: Int = 1,
    val currentSelectedLevel: Int = 1,
    val totalScore: Int = 0,
    val totalArrowsCleared: Int = 0,
    val perfectThreeHeartClears: Int = 0,
    val bestComboStreak: Int = 0,
    val bossesDefeated: Int = 0,
    val dailyChallengesWon: Int = 0,
    val endlessRoundsWon: Int = 0,
    val showRayGuide: Boolean = false,
    val showYarnStitchRibs: Boolean = true,
    val sfxEnabled: Boolean = true,
    val calmingMusicEnabled: Boolean = true,
    val arrowPinStyle: ArrowPinStyle = ArrowPinStyle.MINIMAL_NARROW,
    val boardBgStyle: BoardBackgroundStyle = BoardBackgroundStyle.WAFFLE_LINEN,
    val darkThemeOverride: Boolean? = null,
    val completedLevels: Set<Int> = emptySet(),
    val levelStars: Map<Int, Int> = emptyMap(),
    val activeBurgerStackIds: List<String> = listOf(
        "bottom_bun"
    )
) {
    val totalStarsEarned: Int
        get() = levelStars.values.sum()
}

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val HIGHEST_UNLOCKED = intPreferencesKey("highest_unlocked_level")
        val CURRENT_SELECTED = intPreferencesKey("current_selected_level")
        val TOTAL_SCORE = intPreferencesKey("total_score")
        val TOTAL_ARROWS = intPreferencesKey("total_arrows_cleared")
        val PERFECT_CLEARS = intPreferencesKey("perfect_clears")
        val BEST_COMBO = intPreferencesKey("best_combo_streak")
        val BOSSES_DEFEATED = intPreferencesKey("bosses_defeated")
        val DAILY_WON = intPreferencesKey("daily_challenges_won")
        val ENDLESS_WON = intPreferencesKey("endless_rounds_won")
        val SHOW_RAY_GUIDE = booleanPreferencesKey("show_ray_guide")
        val SHOW_YARN_RIBS = booleanPreferencesKey("show_yarn_ribs")
        val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("calming_music_enabled")
        val ARROW_PIN_STYLE = intPreferencesKey("arrow_pin_style")
        val BOARD_BG_STYLE = intPreferencesKey("board_bg_style")
        val DARK_THEME_MODE = intPreferencesKey("dark_theme_mode") // 0 = system, 1 = light, 2 = dark
        val COMPLETED_LEVELS = stringSetPreferencesKey("completed_levels")
        val LEVEL_STARS = stringSetPreferencesKey("level_stars")
        val ACTIVE_STACK = stringSetPreferencesKey("active_burger_stack")
    }

    val playerProgressFlow: Flow<PlayerProgressState> = context.dataStore.data.map { prefs ->
        val highest = prefs[Keys.HIGHEST_UNLOCKED] ?: 1
        val current = (prefs[Keys.CURRENT_SELECTED] ?: 1).coerceIn(1, highest)
        val completedSet = (prefs[Keys.COMPLETED_LEVELS] ?: emptySet())
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        val starsMap = mutableMapOf<Int, Int>()
        (prefs[Keys.LEVEL_STARS] ?: emptySet()).forEach { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                val lvl = parts[0].toIntOrNull()
                val stars = parts[1].toIntOrNull()
                if (lvl != null && stars != null) {
                    starsMap[lvl] = stars.coerceIn(1, 3)
                }
            }
        }
        // Ensure any completed level without explicit stars entry defaults to at least 1 star
        completedSet.forEach { lvl ->
            if (lvl !in starsMap) {
                starsMap[lvl] = 2
            }
        }

        val themeCode = prefs[Keys.DARK_THEME_MODE] ?: 0
        val darkOverride = when (themeCode) {
            1 -> false
            2 -> true
            else -> null
        }
        val pinStyleIdx = (prefs[Keys.ARROW_PIN_STYLE] ?: 0)
            .coerceIn(0, ArrowPinStyle.entries.lastIndex)
        val bgStyleIdx = (prefs[Keys.BOARD_BG_STYLE] ?: 0)
            .coerceIn(0, BoardBackgroundStyle.entries.lastIndex)

        val savedStack = prefs[Keys.ACTIVE_STACK]
        val defaultStackIds = LevelCatalog.burgerCollectibles
            .filter { it.unlockedAtLevel <= highest }
            .map { it.id }
            .ifEmpty { listOf("bottom_bun") }

        val activeStackIds = if (savedStack == null) {
            defaultStackIds
        } else {
            LevelCatalog.burgerCollectibles
                .filter { it.unlockedAtLevel <= highest && it.id in savedStack }
                .map { it.id }
                .ifEmpty { defaultStackIds }
        }

        PlayerProgressState(
            highestUnlockedLevel = highest,
            currentSelectedLevel = current,
            totalScore = prefs[Keys.TOTAL_SCORE] ?: 0,
            totalArrowsCleared = prefs[Keys.TOTAL_ARROWS] ?: 0,
            perfectThreeHeartClears = prefs[Keys.PERFECT_CLEARS] ?: 0,
            bestComboStreak = prefs[Keys.BEST_COMBO] ?: 0,
            bossesDefeated = prefs[Keys.BOSSES_DEFEATED] ?: 0,
            dailyChallengesWon = prefs[Keys.DAILY_WON] ?: 0,
            endlessRoundsWon = prefs[Keys.ENDLESS_WON] ?: 0,
            showRayGuide = prefs[Keys.SHOW_RAY_GUIDE] ?: false,
            showYarnStitchRibs = prefs[Keys.SHOW_YARN_RIBS] ?: true,
            sfxEnabled = prefs[Keys.SFX_ENABLED] ?: true,
            calmingMusicEnabled = prefs[Keys.MUSIC_ENABLED] ?: true,
            arrowPinStyle = ArrowPinStyle.entries[pinStyleIdx],
            boardBgStyle = BoardBackgroundStyle.entries[bgStyleIdx],
            darkThemeOverride = darkOverride,
            completedLevels = completedSet,
            levelStars = starsMap,
            activeBurgerStackIds = activeStackIds
        )
    }

    suspend fun recordArrowCleared(newCombo: Int, scoreEarned: Int) {
        context.dataStore.edit { prefs ->
            val currentTotal = prefs[Keys.TOTAL_ARROWS] ?: 0
            prefs[Keys.TOTAL_ARROWS] = currentTotal + 1
            prefs[Keys.TOTAL_SCORE] = (prefs[Keys.TOTAL_SCORE] ?: 0) + scoreEarned
            val prevBestCombo = prefs[Keys.BEST_COMBO] ?: 0
            if (newCombo > prevBestCombo) {
                prefs[Keys.BEST_COMBO] = newCombo
            }
        }
    }

    suspend fun recordLevelCompleted(
        levelNumber: Int,
        starsEarned: Int,
        isDaily: Boolean,
        isEndless: Boolean,
        isBossLevel: Boolean
    ) {
        context.dataStore.edit { prefs ->
            if (starsEarned >= 3) {
                prefs[Keys.PERFECT_CLEARS] = (prefs[Keys.PERFECT_CLEARS] ?: 0) + 1
            }
            if (isBossLevel) {
                prefs[Keys.BOSSES_DEFEATED] = (prefs[Keys.BOSSES_DEFEATED] ?: 0) + 1
            }
            if (isDaily) {
                prefs[Keys.DAILY_WON] = (prefs[Keys.DAILY_WON] ?: 0) + 1
            } else if (isEndless) {
                prefs[Keys.ENDLESS_WON] = (prefs[Keys.ENDLESS_WON] ?: 0) + 1
            } else if (levelNumber <= LevelCatalog.totalStoryLevels) {
                val completed = (prefs[Keys.COMPLETED_LEVELS] ?: emptySet()).toMutableSet()
                completed.add(levelNumber.toString())
                prefs[Keys.COMPLETED_LEVELS] = completed

                // Update best stars earned for this level
                val currentStarsSet = (prefs[Keys.LEVEL_STARS] ?: emptySet()).toMutableSet()
                val existingMap = mutableMapOf<Int, Int>()
                currentStarsSet.forEach { entry ->
                    val parts = entry.split(":")
                    if (parts.size == 2) {
                        val l = parts[0].toIntOrNull()
                        val s = parts[1].toIntOrNull()
                        if (l != null && s != null) existingMap[l] = s
                    }
                }
                val prevStars = existingMap[levelNumber] ?: 0
                existingMap[levelNumber] = maxOf(prevStars, starsEarned.coerceIn(1, 3))
                prefs[Keys.LEVEL_STARS] = existingMap.entries.map { "${it.key}:${it.value}" }.toSet()

                val prevHighest = prefs[Keys.HIGHEST_UNLOCKED] ?: 1
                val nextLevel = (levelNumber + 1).coerceAtMost(LevelCatalog.totalStoryLevels)
                if (nextLevel > prevHighest) {
                    prefs[Keys.HIGHEST_UNLOCKED] = nextLevel
                    val existingStack = prefs[Keys.ACTIVE_STACK]?.toMutableSet()
                    if (existingStack != null) {
                        LevelCatalog.burgerCollectibles
                            .filter { it.unlockedAtLevel == nextLevel }
                            .forEach { existingStack.add(it.id) }
                        prefs[Keys.ACTIVE_STACK] = existingStack
                    }
                }
            }
        }
    }

    suspend fun setSelectedLevel(levelNumber: Int) {
        context.dataStore.edit { prefs ->
            val highest = prefs[Keys.HIGHEST_UNLOCKED] ?: 1
            prefs[Keys.CURRENT_SELECTED] = levelNumber.coerceIn(1, highest)
        }
    }

    suspend fun toggleRayGuide(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_RAY_GUIDE] = enabled
        }
    }

    suspend fun toggleYarnStitchRibs(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_YARN_RIBS] = enabled
        }
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SFX_ENABLED] = enabled
        }
    }

    suspend fun setCalmingMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MUSIC_ENABLED] = enabled
        }
    }

    suspend fun setArrowPinStyle(style: ArrowPinStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ARROW_PIN_STYLE] = style.ordinal
        }
    }

    suspend fun setBoardBgStyle(style: BoardBackgroundStyle) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BOARD_BG_STYLE] = style.ordinal
        }
    }

    suspend fun setDarkThemeOverride(darkOverride: Boolean?) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME_MODE] = when (darkOverride) {
                false -> 1
                true -> 2
                null -> 0
            }
        }
    }

    suspend fun toggleCollectibleInStack(layerId: String, currentList: List<String>) {
        context.dataStore.edit { prefs ->
            val set = currentList.toMutableSet()
            if (layerId in set && set.size > 1) {
                set.remove(layerId)
            } else {
                set.add(layerId)
            }
            prefs[Keys.ACTIVE_STACK] = set
        }
    }

    suspend fun resetAllProgress() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
