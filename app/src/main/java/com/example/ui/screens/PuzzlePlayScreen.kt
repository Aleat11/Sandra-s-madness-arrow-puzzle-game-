package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audio.MusicTrack
import com.example.data.LevelCatalog
import com.example.data.PlayerProgressState
import com.example.model.BossType
import com.example.model.CrewMember
import com.example.model.GridCell
import com.example.model.PowerUpType
import com.example.ui.BoardUiState
import com.example.ui.components.ArrowBoardCanvas
import com.example.ui.components.CrewAvatarBadge
import com.example.ui.components.CrochetBurgerScoreBadge
import com.example.ui.components.TurnBasedBossArenaCard

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PuzzlePlayScreen(
    boardState: BoardUiState,
    progressState: PlayerProgressState,
    currentSong: MusicTrack,
    isDarkTheme: Boolean,
    onCellTapped: (GridCell) -> Unit,
    onActivatePowerUp: (PowerUpType) -> Unit,
    onRequestHint: () -> Unit,
    onStitchHeart: () -> Unit,
    onRestartLevel: () -> Unit,
    onNextLevel: () -> Unit,
    onSelectStoryLevel: (Int) -> Unit,
    onStartBossFight: (BossType) -> Unit,
    onSwitchSong: () -> Unit,
    onTriggerJerkyShout: () -> Unit,
    onToggleRayGuide: (Boolean) -> Unit,
    onToggleSfx: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val level = boardState.levelConfig
    val scrollState = rememberScrollState()
    val effectiveRayGuide = level.allowRayGuide && progressState.showRayGuide

    val clearKeyholeCount = remember(boardState.remainingArrows, level.gridCols, level.gridRows) {
        LevelCatalog.countCurrentlyClearArrows(
            arrows = boardState.remainingArrows,
            cols = level.gridCols,
            rows = level.gridRows
        )
    }

    val messyHouseUnlocked = LevelCatalog.isBossUnlocked(
        bossType = BossType.MESSY_HOUSE,
        highestUnlockedLevel = progressState.highestUnlockedLevel,
        completedLevels = progressState.completedLevels
    )
    val brokenAcUnlocked = LevelCatalog.isBossUnlocked(
        bossType = BossType.BROKEN_AC,
        highestUnlockedLevel = progressState.highestUnlockedLevel,
        completedLevels = progressState.completedLevels
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 620.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Sandra's Header with Real Photo Avatar, Hilarious SFX & Auto-Switching Song Bar
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CrewAvatarBadge(member = CrewMember.SANDRA, size = 44.dp)
                            Column {
                                Text(
                                    text = "Sandra's Madness",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Lv.${progressState.highestUnlockedLevel}/${LevelCatalog.totalStoryLevels} Unlocked • ⭐${progressState.totalStarsEarned} • 🎵 ${currentSong.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Hilarious SFX, Music Toggle & Switch Song Buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onTriggerJerkyShout,
                                modifier = Modifier.testTag("jerky_voice_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Sandra: Bring Home The Jerky!",
                                    tint = Color(0xFFE53935)
                                )
                            }
                            IconButton(
                                onClick = { onToggleSfx(!progressState.sfxEnabled) },
                                modifier = Modifier.testTag("toggle_sfx_icon_button")
                            ) {
                                Icon(
                                    imageVector = if (progressState.sfxEnabled) {
                                        Icons.AutoMirrored.Filled.VolumeUp
                                    } else {
                                        Icons.AutoMirrored.Filled.VolumeOff
                                    },
                                    contentDescription = "Toggle Hilarious Sound Effects",
                                    tint = if (progressState.sfxEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                )
                            }
                            IconButton(
                                onClick = { onToggleMusic(!progressState.calmingMusicEnabled) },
                                modifier = Modifier.testTag("toggle_music_icon_button")
                            ) {
                                Icon(
                                    imageVector = if (progressState.calmingMusicEnabled) {
                                        Icons.Default.MusicNote
                                    } else {
                                        Icons.Default.MusicOff
                                    },
                                    contentDescription = "Toggle Calming Music",
                                    tint = if (progressState.calmingMusicEnabled) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                )
                            }
                            IconButton(
                                onClick = onSwitchSong,
                                modifier = Modifier.testTag("switch_song_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Switch Song",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // Score & Earned Boss Fight Launch Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CrochetBurgerScoreBadge(
                            score = boardState.levelScore,
                            comboStreak = boardState.comboStreak,
                            hasShield = boardState.hasBriocheShield
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = level.bossType == BossType.MESSY_HOUSE,
                                onClick = { onStartBossFight(BossType.MESSY_HOUSE) },
                                label = {
                                    Text(if (messyHouseUnlocked) "Messy House" else "🔒 House (Lv.5)")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (messyHouseUnlocked) Icons.Default.Home else Icons.Default.Lock,
                                        contentDescription = "Messy House Boss",
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                modifier = Modifier.testTag("quick_boss_house_chip")
                            )
                            FilterChip(
                                selected = level.bossType == BossType.BROKEN_AC,
                                onClick = { onStartBossFight(BossType.BROKEN_AC) },
                                label = {
                                    Text(if (brokenAcUnlocked) "Broken AC" else "🔒 AC (Lv.10)")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (brokenAcUnlocked) Icons.Default.AcUnit else Icons.Default.Lock,
                                        contentDescription = "Broken AC Boss",
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                modifier = Modifier.testTag("quick_boss_ac_chip")
                            )
                        }
                    }
                }
            }

            // 2. Turn-Based Boss Arena Header (Shown when fighting Messy House / Broken AC!)
            if (level.bossType != null) {
                TurnBasedBossArenaCard(
                    bossType = level.bossType,
                    currentHp = boardState.bossCurrentHp,
                    maxHp = boardState.bossMaxHp,
                    movesLeftInTurn = boardState.playerMovesLeftInTurn,
                    lastDamage = boardState.lastBossDamage,
                    statusBanner = boardState.bossStatusBanner,
                    modifier = Modifier.testTag("boss_arena_card")
                )
            }

            // 3. Progressive Level Bar + Hearts + Keyhole Counter + Pro Mode / Ray Guide
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (!level.isDailyChallenge && !level.isEndlessMode && level.levelNumber <= LevelCatalog.totalStoryLevels) {
                            IconButton(
                                onClick = {
                                    if (level.levelNumber > 1) {
                                        onSelectStoryLevel(level.levelNumber - 1)
                                    }
                                },
                                enabled = level.levelNumber > 1,
                                modifier = Modifier.testTag("prev_level_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous Level"
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${level.chapterName} • ${level.accentBadge}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (level.isDailyChallenge || level.isEndlessMode || level.levelNumber > 200) {
                                    level.title
                                } else {
                                    "Level ${level.levelNumber}: ${level.title}"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.testTag("current_level_title")
                            )
                            Text(
                                text = "${level.shapeName} (${level.gridCols}×${level.gridRows} • Par ${level.parMoves} Moves)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!level.isDailyChallenge && !level.isEndlessMode && level.levelNumber <= LevelCatalog.totalStoryLevels) {
                            // Next level button is ONLY unlocked if the player has ALREADY earned the next level!
                            val nextLevelNum = level.levelNumber + 1
                            val isNextLevelUnlocked = nextLevelNum <= LevelCatalog.totalStoryLevels &&
                                LevelCatalog.isStoryLevelUnlocked(nextLevelNum, progressState.highestUnlockedLevel)
                            IconButton(
                                onClick = { onSelectStoryLevel(nextLevelNum) },
                                enabled = nextLevelNum <= LevelCatalog.totalStoryLevels,
                                modifier = Modifier.testTag("next_level_nav_button")
                            ) {
                                Icon(
                                    imageVector = if (isNextLevelUnlocked) {
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight
                                    } else {
                                        Icons.Default.Lock
                                    },
                                    contentDescription = if (isNextLevelUnlocked) {
                                        "Next Unlocked Level"
                                    } else {
                                        "Locked — Beat Current Level First"
                                    },
                                    tint = if (isNextLevelUnlocked) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.testTag("hearts_row")
                        ) {
                            for (i in 1..boardState.maxHearts) {
                                val filled = i <= boardState.heartsRemaining
                                Icon(
                                    imageVector = if (filled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (filled) "Full Crochet Heart" else "Lost Heart",
                                    tint = if (filled) Color(0xFFE53935) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = "${boardState.heartsRemaining}/${boardState.maxHearts}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Left: ${boardState.remainingArrows.size} • Open: $clearKeyholeCount",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                    .testTag("arrows_remaining_text")
                            )
                        }

                        FilterChip(
                            selected = effectiveRayGuide,
                            onClick = { onToggleRayGuide(!progressState.showRayGuide) },
                            label = {
                                Text(if (level.allowRayGuide) "Ray Guide" else "Pro Mode")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (level.allowRayGuide) Icons.Default.Visibility else Icons.Default.Lock,
                                    contentDescription = "Toggle Ray Guide",
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            modifier = Modifier.testTag("ray_guide_chip")
                        )
                    }
                }
            }

            // 4. Sandra, Naty & Ally Live Companion Strip + Hilarious Sound Comic Pop Pill!
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CrewAvatarBadge(member = boardState.activeSpeaker, size = 42.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${boardState.activeSpeaker.displayName} • ${boardState.activeSpeaker.hobbyTag}",
                            style = MaterialTheme.typography.labelMedium,
                            color = boardState.activeSpeaker.badgeColor
                        )
                        Text(
                            text = boardState.companionMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    val soundBadge = boardState.lastHilariousSound
                    if (soundBadge != null) {
                        Surface(
                            color = Color(soundBadge.badgeColorHex),
                            shape = CircleShape
                        ) {
                            Text(
                                text = soundBadge.comicText,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 5. Main Arrow Puzzle Board + Launch Explosions + Win/Loss Overlays
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ArrowBoardCanvas(
                    gridCols = level.gridCols,
                    gridRows = level.gridRows,
                    arrows = boardState.remainingArrows,
                    exitingArrows = boardState.exitingArrows,
                    launchExplosions = boardState.launchExplosions,
                    clearedCells = boardState.clearedCells,
                    bossHazardCells = boardState.bossHazardCells,
                    stitchPattern = level.stitchPattern,
                    comboStreak = boardState.comboStreak,
                    hintedArrowId = boardState.hintedArrowId,
                    xrayArrowIds = boardState.xrayArrowIds,
                    isSpatulaModeActive = boardState.isSpatulaModeActive,
                    blockedAnim = boardState.blockedAnim,
                    showRayGuide = effectiveRayGuide,
                    showYarnStitchRibs = progressState.showYarnStitchRibs,
                    arrowPinStyle = progressState.arrowPinStyle,
                    boardBgStyle = progressState.boardBgStyle,
                    isDarkTheme = isDarkTheme,
                    onCellTapped = onCellTapped
                )

                // Floating Comic Explosion Banner when an arrow just launched!
                this@Column.AnimatedVisibility(
                    visible = boardState.launchExplosions.isNotEmpty() && !boardState.isLevelWon,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    val latest = boardState.launchExplosions.lastOrNull()
                    if (latest != null) {
                        Surface(
                            color = latest.primaryColor,
                            shape = CircleShape,
                            shadowElevation = 6.dp,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = if (latest.comboCount >= 2) {
                                    "💥 ${latest.comicText} • ${latest.comboCount}x COMBO!!"
                                } else {
                                    "💥 ${latest.comicText}"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = boardState.isLevelWon,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    LevelWonOverlayCard(
                        levelTitle = level.title,
                        shapeName = level.shapeName,
                        heartsRemaining = boardState.heartsRemaining,
                        maxHearts = boardState.maxHearts,
                        starsEarned = boardState.earnedStarsOnWin,
                        unlockedBanner = boardState.unlockedNextLevelBanner,
                        bossDefeated = level.bossType?.bossName,
                        onNextLevel = onNextLevel,
                        onReplay = onRestartLevel
                    )
                }

                this@Column.AnimatedVisibility(
                    visible = boardState.isLevelLost,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    LevelLostOverlayCard(
                        emergencyAvailable = boardState.emergencyStitchAvailable,
                        onStitchHeart = onStitchHeart,
                        onTryAgain = onRestartLevel
                    )
                }
            }

            // 6. Scarce Crochet-Hamburger Power-Ups Bar (Earn charges via 8x Combos & 3-Star Clears!)
            PowerUpsDockCard(
                powerUpCounts = boardState.powerUpCounts,
                isSpatulaArmed = boardState.isSpatulaModeActive,
                enabled = !boardState.isLevelWon && !boardState.isLevelLost,
                onActivatePowerUp = onActivatePowerUp
            )

            // 7. Tactical Helper Row (Bring Home The Jerky!, 1 Tactical Hint, 1 Emergency Stitch, Reset)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onTriggerJerkyShout,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("bring_home_jerky_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Bring Home The Jerky",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bring Home The Jerky! 🥩")
                }

                FilledTonalButton(
                    onClick = onRequestHint,
                    enabled = !boardState.isLevelWon && !boardState.isLevelLost && boardState.hintsRemainingInLevel > 0,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("hint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Naty's Hint",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hint (${boardState.hintsRemainingInLevel}/1)")
                }

                FilledTonalButton(
                    onClick = onStitchHeart,
                    enabled = boardState.heartsRemaining < boardState.maxHearts &&
                        boardState.emergencyStitchAvailable &&
                        !boardState.isLevelWon,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("heal_heart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Emergency Stitch Heart",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (boardState.emergencyStitchAvailable) "1x Stitch (1⭐)" else "Stitch Used")
                }

                OutlinedButton(
                    onClick = onRestartLevel,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("restart_level_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Board",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun PowerUpsDockCard(
    powerUpCounts: Map<PowerUpType, Int>,
    isSpatulaArmed: Boolean,
    enabled: Boolean,
    onActivatePowerUp: (PowerUpType) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Earned Power-Up Stash (Earn +1 charge for 3⭐ Flawless Clears or 8x Combos!)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PowerUpType.entries.forEach { powerUp ->
                    val count = powerUpCounts[powerUp] ?: 0
                    val isArmed = powerUp == PowerUpType.SPATULA_SNIP && isSpatulaArmed
                    val icon = when (powerUp) {
                        PowerUpType.SPATULA_SNIP -> Icons.Default.AutoFixHigh
                        PowerUpType.YARN_BOMB -> Icons.Default.Bolt
                        PowerUpType.XRAY_SPECS -> Icons.Default.Visibility
                        PowerUpType.WARM_BRIOCHE -> Icons.Default.Shield
                    }
                    Surface(
                        onClick = { onActivatePowerUp(powerUp) },
                        enabled = enabled && count > 0,
                        color = if (isArmed) {
                            powerUp.accentColor
                        } else {
                            powerUp.accentColor.copy(alpha = if (count > 0) 0.14f else 0.05f)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = if (isArmed) 2.dp else 1.dp,
                                color = powerUp.accentColor.copy(alpha = if (count > 0) 0.6f else 0.2f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("powerup_${powerUp.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = powerUp.title,
                                    tint = if (isArmed) Color.White else powerUp.accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "×$count",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isArmed) Color.White else powerUp.accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = powerUp.shortLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isArmed) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelWonOverlayCard(
    levelTitle: String,
    shapeName: String,
    heartsRemaining: Int,
    maxHearts: Int,
    starsEarned: Int,
    unlockedBanner: String?,
    bossDefeated: String?,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
            .testTag("level_won_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..3) {
                    Icon(
                        imageVector = if (i <= starsEarned) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Text(
                text = if (bossDefeated != null) "Boss Defeated: $bossDefeated!" else "BRING HOME THE JERKY!! 🥩",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Cleared '$levelTitle' ($shapeName) • $heartsRemaining/$maxHearts Hearts • $starsEarned/3 Stars!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (unlockedBanner != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ) {
                    Text(
                        text = unlockedBanner,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                }
            }
            if (starsEarned == 3) {
                Text(
                    text = "🎁 Flawless 3⭐ Bonus: +1 Power-Up Charge Earned!",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF16A34A),
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                OutlinedButton(
                    onClick = onReplay,
                    modifier = Modifier.testTag("replay_level_button")
                ) {
                    Text("Replay")
                }
                Button(
                    onClick = onNextLevel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("next_level_overlay_button")
                ) {
                    Text("Next Earned Level")
                }
            }
        }
    }
}

@Composable
private fun LevelLostOverlayCard(
    emergencyAvailable: Boolean,
    onStitchHeart: () -> Unit,
    onTryAgain: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(24.dp))
            .testTag("level_lost_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Yarn Locked & Tangled!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = if (emergencyAvailable) {
                    "You ran out of Crochet Hearts! Restart the board for a full 3-Star mastery run, or use your 1 Emergency Stitch (caps level at 1⭐)."
                } else {
                    "You already used your 1 Emergency Stitch on this board! Tap 'Restart Board' to untangle this puzzle cleanly!"
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                OutlinedButton(
                    onClick = onTryAgain,
                    modifier = Modifier.testTag("try_again_button")
                ) {
                    Text("Restart Board")
                }
                if (emergencyAvailable) {
                    Button(
                        onClick = onStitchHeart,
                        modifier = Modifier.testTag("continue_with_heart_button")
                    ) {
                        Text("1x Stitch (1⭐ Max)")
                    }
                }
            }
        }
    }
}
