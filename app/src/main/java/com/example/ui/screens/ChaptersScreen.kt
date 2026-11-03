package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LevelCatalog
import com.example.data.PlayerProgressState
import com.example.model.BossType
import com.example.model.LevelConfig
import com.example.ui.components.CrewAvatarBadge
import com.example.ui.components.SandraCafeHeroArt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChaptersScreen(
    progressState: PlayerProgressState,
    selectedEndlessSize: Int,
    onSelectStoryLevel: (Int) -> Unit,
    onStartBossBattle: (BossType) -> Unit,
    onStartDailyChallenge: () -> Unit,
    onStartEndlessMode: (Int) -> Unit,
    onBackToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToPlay() }

    val allLevels = remember { LevelCatalog.getAllStoryLevelSummaries() }
    val groupedByChapter = remember(allLevels) {
        allLevels.groupBy { it.chapterName }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(contentAlignment = Alignment.Center) {
                    SandraCafeHeroArt()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 42.dp)
                    ) {
                        Text(
                            text = "Progressive Chapters & Boss Arena",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFFFFFBEB)
                        )
                        Text(
                            text = "Unlocked: Lv. ${progressState.highestUnlockedLevel}/${LevelCatalog.totalStoryLevels} • ⭐ ${progressState.totalStarsEarned}/${LevelCatalog.totalStoryLevels * 3} Stars • ${progressState.bossesDefeated} Bosses Defeated",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }
            }

            // 1. Progressive Story Chapters 1 to 4 (Must beat each level to unlock the next!)
            groupedByChapter.forEach { (chapterTitle, chapterLevels) ->
                val firstLevelInChapter = chapterLevels.first().levelNumber
                val isChapterUnlocked = LevelCatalog.isStoryLevelUnlocked(
                    levelNumber = firstLevelInChapter,
                    highestUnlockedLevel = progressState.highestUnlockedLevel
                )

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = chapterTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (isChapterUnlocked) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                        if (!isChapterUnlocked) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "🔒 Beat Lv. ${firstLevelInChapter - 1} Boss",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                items(chapterLevels, key = { it.levelNumber }) { level ->
                    val isUnlocked = LevelCatalog.isStoryLevelUnlocked(
                        levelNumber = level.levelNumber,
                        highestUnlockedLevel = progressState.highestUnlockedLevel
                    )
                    val isCompleted = level.levelNumber in progressState.completedLevels
                    val starsEarned = progressState.levelStars[level.levelNumber] ?: 0
                    StoryLevelCard(
                        level = level,
                        isUnlocked = isUnlocked,
                        isCompleted = isCompleted,
                        starsEarned = starsEarned,
                        onPlayClicked = { onSelectStoryLevel(level.levelNumber) }
                    )
                }
            }

            // 2. Dedicated Turn-Based Boss Fights Section (Each Boss Unlocks via Story Progression!)
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Turn-Based Boss Arena (Earn Each Boss by Level!)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Every arrow you clear strikes the Boss! Blocked mistakes heal the Boss +20 HP and waste a turn move.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                        )

                        BossType.entries.forEach { boss ->
                            val isBossUnlocked = LevelCatalog.isBossUnlocked(
                                bossType = boss,
                                highestUnlockedLevel = progressState.highestUnlockedLevel,
                                completedLevels = progressState.completedLevels
                            )
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(if (isBossUnlocked) 1f else 0.72f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = boss.bossName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = if (isBossUnlocked) boss.badgeColor else MaterialTheme.colorScheme.outline,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isBossUnlocked) {
                                                "${boss.subtitle} • ${boss.maxHp} HP • 2 Hearts"
                                            } else {
                                                "🔒 Locked — Reach Story Level ${boss.unlockLevel} to unlock!"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Button(
                                        onClick = { onStartBossBattle(boss) },
                                        enabled = isBossUnlocked,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = boss.badgeColor
                                        ),
                                        modifier = Modifier.testTag("launch_boss_${boss.name.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = if (isBossUnlocked) Icons.Default.Bolt else Icons.Default.Lock,
                                            contentDescription = if (isBossUnlocked) "Fight Boss" else "Locked Boss",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBossUnlocked) "Fight" else "Lv. ${boss.unlockLevel}")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Sandra's Endless Madness Oven (Progressive Grid Unlocks up to 10x10!)
            item {
                val isCurrentSizeUnlocked = LevelCatalog.isEndlessSizeUnlocked(
                    gridSize = selectedEndlessSize,
                    highestUnlockedLevel = progressState.highestUnlockedLevel
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sandra's Strategic Endless Grids (Earn up to 10×10!)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Single-keyhole interlocking grids • Beat Story Levels to unlock larger sizes (${progressState.endlessRoundsWon} won)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            FilledTonalButton(
                                onClick = { onStartEndlessMode(selectedEndlessSize) },
                                enabled = isCurrentSizeUnlocked,
                                modifier = Modifier.testTag("start_endless_mode_button")
                            ) {
                                Icon(
                                    imageVector = if (isCurrentSizeUnlocked) Icons.Default.AutoAwesome else Icons.Default.Lock,
                                    contentDescription = "Generate Endless Puzzle",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (isCurrentSizeUnlocked) {
                                        "Play ${selectedEndlessSize}×$selectedEndlessSize"
                                    } else {
                                        "Lv. ${LevelCatalog.requiredLevelForEndlessSize(selectedEndlessSize)}"
                                    }
                                )
                            }
                        }

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(5, 6, 7, 8, 9, 10).forEach { sizeOption ->
                                val sizeUnlocked = LevelCatalog.isEndlessSizeUnlocked(
                                    gridSize = sizeOption,
                                    highestUnlockedLevel = progressState.highestUnlockedLevel
                                )
                                val reqLvl = LevelCatalog.requiredLevelForEndlessSize(sizeOption)
                                FilterChip(
                                    selected = selectedEndlessSize == sizeOption,
                                    onClick = { onStartEndlessMode(sizeOption) },
                                    label = {
                                        Text(
                                            if (sizeUnlocked) {
                                                "${sizeOption}×$sizeOption"
                                            } else {
                                                "🔒 ${sizeOption}×$sizeOption (Lv.$reqLvl)"
                                            }
                                        )
                                    },
                                    modifier = Modifier.testTag("endless_size_${sizeOption}_chip")
                                )
                            }
                        }
                    }
                }
            }

            // 4. Ally's Daily 9x9 Burger Challenge (Unlocks at Story Level 8)
            item {
                val dailyUnlocked = LevelCatalog.isDailyChallengeUnlocked(progressState.highestUnlockedLevel)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (dailyUnlocked) 1f else 0.75f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (dailyUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                            contentDescription = "Daily Challenge Trophy",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(38.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ally's Daily 9×9 Master Trophy",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = if (dailyUnlocked) {
                                    "Hard 9×9 Single-Keyhole Challenge (2 Hearts) • Trophies: ${progressState.dailyChallengesWon}"
                                } else {
                                    "🔒 Unlocks at Story Level ${LevelCatalog.DAILY_CHALLENGE_UNLOCK_LEVEL} • Trophies: ${progressState.dailyChallengesWon}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                            )
                        }
                        Button(
                            onClick = onStartDailyChallenge,
                            enabled = dailyUnlocked,
                            modifier = Modifier.testTag("start_daily_challenge_button")
                        ) {
                            Text(if (dailyUnlocked) "Play Daily" else "Lv. ${LevelCatalog.DAILY_CHALLENGE_UNLOCK_LEVEL}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryLevelCard(
    level: LevelConfig,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    starsEarned: Int,
    onPlayClicked: () -> Unit
) {
    Card(
        onClick = { if (isUnlocked) onPlayClicked() },
        enabled = isUnlocked,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                isUnlocked -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isUnlocked) 1f else 0.68f)
            .testTag("story_level_card_${level.levelNumber}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CrewAvatarBadge(member = level.host, size = 44.dp)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Level ${level.levelNumber}: ${level.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isUnlocked) {
                        "${level.shapeName} • ${level.gridCols}×${level.gridRows} • ${level.arrows.size} Arrows • ${level.startingHearts} Hearts"
                    } else {
                        "🔒 Locked — Beat Level ${level.levelNumber - 1} to unlock (${level.gridCols}×${level.gridRows} Grid)"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = (level.bossType?.badgeColor ?: level.host.badgeColor).copy(alpha = 0.14f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${level.accentBadge} • ${ if (level.allowRayGuide) "Ray Guide OK" else "No Free Guide" }",
                            style = MaterialTheme.typography.labelMedium,
                            color = level.bossType?.badgeColor ?: level.host.badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (isCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = if (s <= starsEarned) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(28.dp)
                    )
                }
                isUnlocked -> {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Level",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Level",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
