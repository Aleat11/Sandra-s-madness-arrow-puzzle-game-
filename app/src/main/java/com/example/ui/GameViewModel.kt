package com.example.ui

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.HilariousArrowSound
import com.example.audio.MusicTrack
import com.example.audio.SoundEffectsManager
import com.example.data.LevelCatalog
import com.example.data.PlayerProgressState
import com.example.data.PreferencesRepository
import com.example.model.ArrowPinStyle
import com.example.model.BoardBackgroundStyle
import com.example.model.BossType
import com.example.model.CrewMember
import com.example.model.GridCell
import com.example.model.LevelConfig
import com.example.model.PowerUpType
import com.example.model.PuzzleArrow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppDestination {
    PLAY,
    CHAPTERS,
    CAFE_KITCHEN,
    CREW_NOOK
}

data class ExitingArrowAnim(
    val arrow: PuzzleArrow,
    val startTimeMs: Long = System.currentTimeMillis()
)

data class LaunchExplosionAnim(
    val id: Long,
    val cells: List<GridCell>,
    val primaryColor: Color,
    val highlightColor: Color,
    val comicText: String,
    val comboCount: Int,
    val headCell: GridCell,
    val startTimeMs: Long = System.currentTimeMillis()
)

data class BlockedArrowAnim(
    val arrowId: Int,
    val blockingCell: GridCell,
    val timestampMs: Long = System.currentTimeMillis()
)

data class BoardUiState(
    val currentTab: AppDestination = AppDestination.PLAY,
    val levelConfig: LevelConfig = LevelCatalog.getStoryLevel(1),
    val remainingArrows: List<PuzzleArrow> = emptyList(),
    val clearedCells: Set<GridCell> = emptySet(),
    val heartsRemaining: Int = 3,
    val maxHearts: Int = 3,
    val comboStreak: Int = 0,
    val levelScore: Int = 0,
    val movesMade: Int = 0,
    val mistakesMade: Int = 0,
    val hintsRemainingInLevel: Int = 1,
    val emergencyStitchAvailable: Boolean = true,
    val usedAssistInLevel: Boolean = false,
    val usedEmergencyStitchInLevel: Boolean = false,
    val earnedStarsOnWin: Int = 3,
    val unlockedNextLevelBanner: String? = null,
    val hintedArrowId: Int? = null,
    val xrayArrowIds: Set<Int> = emptySet(),
    val isSpatulaModeActive: Boolean = false,
    val hasBriocheShield: Boolean = false,
    // Persistent, scarce power-ups earned through skillful combos & 3-star clears!
    val powerUpCounts: Map<PowerUpType, Int> = mapOf(
        PowerUpType.SPATULA_SNIP to 1,
        PowerUpType.YARN_BOMB to 1,
        PowerUpType.XRAY_SPECS to 1,
        PowerUpType.WARM_BRIOCHE to 1
    ),
    // Hilarious Sound & Combo Callout Banner
    val lastHilariousSound: HilariousArrowSound? = null,
    val launchExplosions: List<LaunchExplosionAnim> = emptyList(),
    // Turn-Based Boss Fight State
    val bossCurrentHp: Int = 0,
    val bossMaxHp: Int = 0,
    val playerMovesLeftInTurn: Int = 3,
    val bossTurnCount: Int = 0,
    val lastBossDamage: Int? = null,
    val bossStatusBanner: String? = null,
    val bossHazardCells: Set<GridCell> = emptySet(),
    // Animations & Overlays
    val blockedAnim: BlockedArrowAnim? = null,
    val exitingArrows: List<ExitingArrowAnim> = emptyList(),
    val isLevelWon: Boolean = false,
    val isLevelLost: Boolean = false,
    val activeSpeaker: CrewMember = CrewMember.SANDRA,
    val companionMessage: String = "",
    val endlessGridSize: Int = 5,
    val endlessRound: Int = 1
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application.applicationContext)
    private val soundManager = SoundEffectsManager(application.applicationContext)

    val currentSongFlow: StateFlow<MusicTrack> = soundManager.currentTrack

    val progressState: StateFlow<PlayerProgressState> = preferencesRepository.playerProgressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerProgressState()
        )

    private val _boardState = MutableStateFlow(BoardUiState())
    val boardState: StateFlow<BoardUiState> = _boardState.asStateFlow()

    private var blockedClearJob: Job? = null
    private var nextExplosionId = 1L

    init {
        viewModelScope.launch {
            val saved = preferencesRepository.playerProgressFlow.first()
            soundManager.setSoundEffectsEnabled(saved.sfxEnabled)
            soundManager.setCalmingMusicEnabled(saved.calmingMusicEnabled)
            val safeLevel = saved.currentSelectedLevel.coerceIn(1, saved.highestUnlockedLevel.coerceAtLeast(1))
            val config = LevelCatalog.getStoryLevel(safeLevel)
            startLevelWithConfig(config, resetPowerUpsIfEmpty = true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }

    fun selectTab(tab: AppDestination) {
        _boardState.update { it.copy(currentTab = tab) }
    }

    fun switchSongNow() {
        soundManager.switchToNextSong()
        val newSong = soundManager.currentTrack.value
        _boardState.update {
            it.copy(
                activeSpeaker = CrewMember.ALLY,
                companionMessage = "Ally switched the cafe radio to '${newSong.title}' (${newSong.vibe})!"
            )
        }
    }

    fun triggerSandraJerkyVoice() {
        soundManager.speakBringHomeTheJerky()
        _boardState.update {
            it.copy(
                lastHilariousSound = HilariousArrowSound.BRING_HOME_THE_JERKY,
                activeSpeaker = CrewMember.SANDRA,
                companionMessage = "Sandra shouts: 'BRING HOME THE JERKY!!' 🥩🔥"
            )
        }
    }

    fun loadStoryLevel(levelNumber: Int, allowJustUnlocked: Int? = null) {
        val highestUnlocked = maxOf(
            progressState.value.highestUnlockedLevel,
            allowJustUnlocked ?: 1
        )
        val clampedTarget = levelNumber.coerceIn(1, LevelCatalog.totalStoryLevels)
        if (!LevelCatalog.isStoryLevelUnlocked(clampedTarget, highestUnlocked)) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.SANDRA,
                    companionMessage = "🔒 Level $clampedTarget is locked! Beat Level $highestUnlocked first—you have to earn the higher levels!"
                )
            }
            return
        }

        val config = LevelCatalog.getStoryLevel(clampedTarget)
        viewModelScope.launch {
            preferencesRepository.setSelectedLevel(clampedTarget)
        }
        startLevelWithConfig(config, resetPowerUpsIfEmpty = false)
    }

    fun startBossBattle(bossType: BossType) {
        val progress = progressState.value
        if (!LevelCatalog.isBossUnlocked(bossType, progress.highestUnlockedLevel, progress.completedLevels)) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.SANDRA,
                    companionMessage = "🔒 ${bossType.bossName} is locked! Reach Story Level ${bossType.unlockLevel} to earn this Turn-Based Boss Fight!"
                )
            }
            return
        }
        val config = LevelCatalog.getBossBattleLevel(bossType)
        startLevelWithConfig(config, resetPowerUpsIfEmpty = false)
    }

    fun startDailyChallenge() {
        val progress = progressState.value
        if (!LevelCatalog.isDailyChallengeUnlocked(progress.highestUnlockedLevel)) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.ALLY,
                    companionMessage = "🔒 Ally's 9×9 Daily Trophy Challenge unlocks at Story Level ${LevelCatalog.DAILY_CHALLENGE_UNLOCK_LEVEL}!"
                )
            }
            return
        }
        val daySeed = System.currentTimeMillis() / (1000L * 60L * 60L * 24L)
        val config = LevelCatalog.getDailyChallengeLevel(daySeed)
        startLevelWithConfig(config, resetPowerUpsIfEmpty = false)
    }

    fun startEndlessMadness(gridSize: Int = _boardState.value.endlessGridSize) {
        val clamped = gridSize.coerceIn(5, 10)
        val progress = progressState.value
        if (!LevelCatalog.isEndlessSizeUnlocked(clamped, progress.highestUnlockedLevel)) {
            val req = LevelCatalog.requiredLevelForEndlessSize(clamped)
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.NATY,
                    companionMessage = "🔒 ${clamped}×$clamped Endless Grids unlock at Story Level $req! Earn your way through the Story Chapters first!"
                )
            }
            return
        }
        val currentRound = _boardState.value.endlessRound
        val config = LevelCatalog.generateEndlessMadnessLevel(
            gridSize = clamped,
            madnessRound = currentRound,
            seed = System.currentTimeMillis()
        )
        _boardState.update { it.copy(endlessGridSize = clamped) }
        startLevelWithConfig(config, resetPowerUpsIfEmpty = false)
    }

    fun nextEndlessRound() {
        val nextRound = _boardState.value.endlessRound + 1
        val gridSize = _boardState.value.endlessGridSize
        _boardState.update { it.copy(endlessRound = nextRound) }
        val config = LevelCatalog.generateEndlessMadnessLevel(
            gridSize = gridSize,
            madnessRound = nextRound,
            seed = System.currentTimeMillis()
        )
        startLevelWithConfig(config, resetPowerUpsIfEmpty = false)
    }

    private fun startLevelWithConfig(
        config: LevelConfig,
        resetPowerUpsIfEmpty: Boolean
    ) {
        blockedClearJob?.cancel()
        val boss = config.bossType
        val initialBossHp = boss?.maxHp ?: 0
        val movesPerTurn = boss?.playerMovesPerTurn ?: 3
        val startHearts = config.startingHearts
        val initialClearCount = LevelCatalog.countCurrentlyClearArrows(
            arrows = config.arrows,
            cols = config.gridCols,
            rows = config.gridRows
        )

        _boardState.update { current ->
            val persistedPowerUps = if (resetPowerUpsIfEmpty) {
                mapOf(
                    PowerUpType.SPATULA_SNIP to 1,
                    PowerUpType.YARN_BOMB to 1,
                    PowerUpType.XRAY_SPECS to 1,
                    PowerUpType.WARM_BRIOCHE to 1
                )
            } else {
                current.powerUpCounts
            }

            current.copy(
                currentTab = AppDestination.PLAY,
                levelConfig = config,
                remainingArrows = config.arrows,
                clearedCells = emptySet(),
                heartsRemaining = startHearts,
                maxHearts = startHearts,
                comboStreak = 0,
                levelScore = 0,
                movesMade = 0,
                mistakesMade = 0,
                hintsRemainingInLevel = 1,
                emergencyStitchAvailable = true,
                usedAssistInLevel = false,
                usedEmergencyStitchInLevel = false,
                earnedStarsOnWin = 3,
                unlockedNextLevelBanner = null,
                hintedArrowId = null,
                xrayArrowIds = emptySet(),
                isSpatulaModeActive = false,
                hasBriocheShield = false,
                powerUpCounts = persistedPowerUps,
                lastHilariousSound = null,
                launchExplosions = emptyList(),
                bossCurrentHp = initialBossHp,
                bossMaxHp = initialBossHp,
                playerMovesLeftInTurn = movesPerTurn,
                bossTurnCount = 0,
                lastBossDamage = null,
                bossStatusBanner = if (boss != null) {
                    "Your Turn! $initialClearCount keyhole opening${if (initialClearCount == 1) "" else "s"} on board • $movesPerTurn launches before ${boss.bossName} counter-attacks!"
                } else {
                    null
                },
                bossHazardCells = emptySet(),
                blockedAnim = null,
                exitingArrows = emptyList(),
                isLevelWon = false,
                isLevelLost = false,
                activeSpeaker = config.host,
                companionMessage = "${config.hostTip} ($initialClearCount keyhole arrow${if (initialClearCount == 1) " is" else "s are"} currently unblocked!)"
            )
        }
    }

    fun restartCurrentLevel() {
        val currentConfig = _boardState.value.levelConfig
        startLevelWithConfig(currentConfig, resetPowerUpsIfEmpty = false)
    }

    fun onCellOrArrowTapped(tappedCell: GridCell) {
        val state = _boardState.value
        if (state.isLevelWon || state.isLevelLost) return

        val tappedArrow = state.remainingArrows.find { arrow ->
            tappedCell in arrow.cells
        } ?: return

        onArrowTapped(tappedArrow)
    }

    fun onArrowTapped(arrow: PuzzleArrow) {
        val state = _boardState.value
        if (state.isLevelWon || state.isLevelLost) return

        if (state.isSpatulaModeActive) {
            soundManager.playPowerUp()
            clearArrowsAndApplyTurn(
                arrowsToClear = listOf(arrow),
                usedSpatula = true
            )
            return
        }

        val blockingCell = LevelCatalog.findFirstBlockingCell(
            arrow = arrow,
            allActiveArrows = state.remainingArrows,
            cols = state.levelConfig.gridCols,
            rows = state.levelConfig.gridRows
        )

        if (blockingCell == null) {
            clearArrowsAndApplyTurn(
                arrowsToClear = listOf(arrow),
                usedSpatula = false
            )
        } else {
            handleBlockedTap(arrow, blockingCell)
        }
    }

    private fun clearArrowsAndApplyTurn(
        arrowsToClear: List<PuzzleArrow>,
        usedSpatula: Boolean
    ) {
        val state = _boardState.value
        val clearIds = arrowsToClear.map { it.id }.toSet()
        val updatedRemaining = state.remainingArrows.filterNot { it.id in clearIds }
        val newlyClearedCells = arrowsToClear.flatMap { it.cells }.toSet()
        val updatedCleared = state.clearedCells + newlyClearedCells

        val newCombo = state.comboStreak + arrowsToClear.size
        val pointsEarned = arrowsToClear.sumOf { it.cells.size * 55 } + (newCombo * 35)
        val isNowWon = updatedRemaining.isEmpty()

        val hilariousSound = soundManager.playHilariousArrowLaunch(newCombo)
        if (isNowWon) {
            soundManager.playLevelVictory()
        }

        val createdExplosions = arrowsToClear.map { arr ->
            LaunchExplosionAnim(
                id = nextExplosionId++,
                cells = arr.cells,
                primaryColor = arr.yarnColor.primaryColor,
                highlightColor = arr.yarnColor.highlightColor,
                comicText = hilariousSound.comicText,
                comboCount = newCombo,
                headCell = arr.headCell
            )
        }
        val explosionIds = createdExplosions.map { it.id }.toSet()

        // Calculate Turn-Based Boss damage if in a Boss Fight
        val boss = state.levelConfig.bossType
        var newBossHp = state.bossCurrentHp
        var damageDealt: Int? = null
        var nextMovesLeft = state.playerMovesLeftInTurn
        var nextBossTurnCount = state.bossTurnCount
        var nextBossBanner = state.bossStatusBanner
        var updatedHazards = state.bossHazardCells - newlyClearedCells

        if (boss != null) {
            val rawDamage = if (isNowWon) {
                state.bossCurrentHp.coerceAtLeast(45)
            } else {
                val perArrowDmg = (state.bossMaxHp / state.levelConfig.arrows.size.coerceAtLeast(1)) + 12
                (perArrowDmg * arrowsToClear.size) + (newCombo * 5) + (if (usedSpatula) 20 else 0)
            }
            damageDealt = rawDamage
            newBossHp = if (isNowWon) 0 else (state.bossCurrentHp - rawDamage).coerceAtLeast(0)
            soundManager.playBossHit(isCritical = newCombo >= 4 || usedSpatula)

            if (!isNowWon && newBossHp > 0) {
                nextMovesLeft = state.playerMovesLeftInTurn - 1
                if (nextMovesLeft <= 0) {
                    nextBossTurnCount += 1
                    nextMovesLeft = boss.playerMovesPerTurn
                    soundManager.playBossTurnAction()
                    val hazardPair = triggerBossTurnHazard(
                        boss = boss,
                        remainingArrows = updatedRemaining,
                        existingHazards = updatedHazards,
                        turnNumber = nextBossTurnCount
                    )
                    updatedHazards = hazardPair.first
                    nextBossBanner = hazardPair.second
                } else {
                    nextBossBanner = "${hilariousSound.comicText} Direct Hit (-$rawDamage HP)! $nextMovesLeft Launch${if (nextMovesLeft == 1) "" else "es"} left in turn!"
                }
            } else {
                newBossHp = 0
                nextBossBanner = boss.victoryQuote
            }
        }

        // X-Ray Specs only peek for 1 move, then clear!
        val updatedPowerUps = state.powerUpCounts.toMutableMap()
        if (newCombo > 0 && newCombo % 8 == 0) {
            val bonusType = PowerUpType.entries[(newCombo / 8) % PowerUpType.entries.size]
            updatedPowerUps[bonusType] = (updatedPowerUps[bonusType] ?: 0) + 1
        }

        val starsEarned = computeStarsForWin(
            mistakesMade = state.mistakesMade,
            usedAssist = state.usedAssistInLevel,
            usedEmergencyStitch = state.usedEmergencyStitchInLevel
        )

        var unlockAnnouncement: String? = null
        if (isNowWon) {
            // Reward a bonus power-up on a 3-Star Flawless Clear!
            if (starsEarned == 3) {
                val rewardType = PowerUpType.entries[state.levelConfig.levelNumber % PowerUpType.entries.size]
                updatedPowerUps[rewardType] = (updatedPowerUps[rewardType] ?: 0) + 1
            }
            val currentLvl = state.levelConfig.levelNumber
            if (!state.levelConfig.isDailyChallenge && !state.levelConfig.isEndlessMode && currentLvl < LevelCatalog.totalStoryLevels) {
                val nextLvl = currentLvl + 1
                if (nextLvl > progressState.value.highestUnlockedLevel) {
                    unlockAnnouncement = "🔓 UNLOCKED Level $nextLvl: ${LevelCatalog.getStoryLevel(nextLvl).title}!"
                }
            }
        }

        val nextOpeningsCount = if (isNowWon) 0 else LevelCatalog.countCurrentlyClearArrows(
            arrows = updatedRemaining,
            cols = state.levelConfig.gridCols,
            rows = state.levelConfig.gridRows
        )

        val (speaker, message) = buildSuccessReaction(
            combo = newCombo,
            remainingCount = updatedRemaining.size,
            openingsCount = nextOpeningsCount,
            isWon = isNowWon,
            starsEarned = starsEarned,
            boss = boss,
            damageDealt = damageDealt,
            hilariousSound = hilariousSound
        )

        _boardState.update { current ->
            current.copy(
                remainingArrows = updatedRemaining,
                clearedCells = updatedCleared,
                comboStreak = newCombo,
                levelScore = current.levelScore + pointsEarned,
                movesMade = current.movesMade + 1,
                earnedStarsOnWin = starsEarned,
                unlockedNextLevelBanner = unlockAnnouncement,
                hintedArrowId = if (current.hintedArrowId in clearIds) null else current.hintedArrowId,
                xrayArrowIds = emptySet(),
                isSpatulaModeActive = false,
                powerUpCounts = updatedPowerUps,
                lastHilariousSound = hilariousSound,
                launchExplosions = current.launchExplosions + createdExplosions,
                bossCurrentHp = newBossHp,
                playerMovesLeftInTurn = nextMovesLeft,
                bossTurnCount = nextBossTurnCount,
                lastBossDamage = damageDealt,
                bossStatusBanner = nextBossBanner,
                bossHazardCells = updatedHazards,
                blockedAnim = null,
                exitingArrows = current.exitingArrows + arrowsToClear.map { ExitingArrowAnim(it) },
                isLevelWon = isNowWon,
                activeSpeaker = speaker,
                companionMessage = message
            )
        }

        viewModelScope.launch {
            preferencesRepository.recordArrowCleared(newCombo, pointsEarned)
            if (isNowWon) {
                preferencesRepository.recordLevelCompleted(
                    levelNumber = state.levelConfig.levelNumber,
                    starsEarned = starsEarned,
                    isDaily = state.levelConfig.isDailyChallenge,
                    isEndless = state.levelConfig.isEndlessMode,
                    isBossLevel = boss != null
                )
            }
            delay(460)
            _boardState.update { current ->
                current.copy(
                    exitingArrows = current.exitingArrows.filterNot { it.arrow.id in clearIds }
                )
            }
            delay(240)
            _boardState.update { current ->
                current.copy(
                    launchExplosions = current.launchExplosions.filterNot { it.id in explosionIds }
                )
            }
        }
    }

    private fun computeStarsForWin(
        mistakesMade: Int,
        usedAssist: Boolean,
        usedEmergencyStitch: Boolean
    ): Int {
        return when {
            usedEmergencyStitch -> 1
            mistakesMade == 0 && !usedAssist -> 3
            mistakesMade <= 1 -> 2
            else -> 1
        }
    }

    private fun handleBlockedTap(arrow: PuzzleArrow, blockingCell: GridCell) {
        val state = _boardState.value
        soundManager.playBlockedBump()

        if (state.hasBriocheShield) {
            _boardState.update { current ->
                current.copy(
                    hasBriocheShield = false,
                    blockedAnim = BlockedArrowAnim(arrow.id, blockingCell),
                    activeSpeaker = CrewMember.NATY,
                    companionMessage = "Naty's Warm Brioche Shield absorbed the blocked bump at row ${blockingCell.row + 1}, col ${blockingCell.col + 1}! Shield consumed!"
                )
            }
            blockedClearJob?.cancel()
            blockedClearJob = viewModelScope.launch {
                delay(650)
                _boardState.update { it.copy(blockedAnim = null) }
            }
            return
        }

        // Hazard cells deal 2 Hearts of penalty if you carelessly tap a blocked hazard arrow!
        val isHazardArrow = arrow.cells.any { it in state.bossHazardCells }
        val heartPenalty = if (isHazardArrow) 2 else 1
        val newHearts = (state.heartsRemaining - heartPenalty).coerceAtLeast(0)
        val isLost = newHearts == 0

        // In Boss Battles, a blocked mistake heals the Boss +20 HP and consumes a turn move!
        val boss = state.levelConfig.bossType
        var updatedBossHp = state.bossCurrentHp
        var updatedBossMovesLeft = state.playerMovesLeftInTurn
        var updatedBossTurn = state.bossTurnCount
        var updatedHazards = state.bossHazardCells
        var updatedBossBanner = state.bossStatusBanner

        if (boss != null && !isLost) {
            updatedBossHp = (state.bossCurrentHp + 20).coerceAtMost(state.bossMaxHp)
            updatedBossMovesLeft = (state.playerMovesLeftInTurn - 1).coerceAtLeast(0)
            if (updatedBossMovesLeft == 0) {
                updatedBossTurn += 1
                updatedBossMovesLeft = boss.playerMovesPerTurn
                soundManager.playBossTurnAction()
                val hazardPair = triggerBossTurnHazard(
                    boss = boss,
                    remainingArrows = state.remainingArrows,
                    existingHazards = updatedHazards,
                    turnNumber = updatedBossTurn
                )
                updatedHazards = hazardPair.first
                updatedBossBanner = "Blocked mistake healed ${boss.bossName} +20 HP! ${hazardPair.second}"
            } else {
                updatedBossBanner = "⚠️ Blocked tap! ${boss.bossName} repaired +20 HP! ($updatedBossMovesLeft move${if (updatedBossMovesLeft == 1) "" else "s"} left)"
            }
        }

        val (speaker, message) = buildBlockedReaction(
            heartsLeft = newHearts,
            blockingCell = blockingCell,
            bossHealed = boss != null
        )

        _boardState.update { current ->
            current.copy(
                heartsRemaining = newHearts,
                comboStreak = 0,
                movesMade = current.movesMade + 1,
                mistakesMade = current.mistakesMade + 1,
                bossCurrentHp = updatedBossHp,
                playerMovesLeftInTurn = updatedBossMovesLeft,
                bossTurnCount = updatedBossTurn,
                bossHazardCells = updatedHazards,
                bossStatusBanner = updatedBossBanner,
                blockedAnim = BlockedArrowAnim(arrow.id, blockingCell),
                isLevelLost = isLost,
                activeSpeaker = speaker,
                companionMessage = message
            )
        }

        blockedClearJob?.cancel()
        blockedClearJob = viewModelScope.launch {
            delay(650)
            _boardState.update { it.copy(blockedAnim = null) }
        }
    }

    private fun triggerBossTurnHazard(
        boss: BossType,
        remainingArrows: List<PuzzleArrow>,
        existingHazards: Set<GridCell>,
        turnNumber: Int
    ): Pair<Set<GridCell>, String> {
        val candidateCells = remainingArrows.flatMap { it.cells }.shuffled().take(3)
        val updatedHazards = existingHazards + candidateCells
        val banner = when (boss) {
            BossType.MESSY_HOUSE ->
                "BOSS TURN #$turnNumber: The Messy House tossed dusty laundry onto ${candidateCells.size} cells (2-Heart penalty if tapped while blocked)!"
            BossType.BROKEN_AC ->
                "BOSS TURN #$turnNumber: The Broken AC blasted ${89 + (turnNumber % 5)}°F heat onto ${candidateCells.size} cells!"
            BossType.LAUNDRY_YARN_TORNADO ->
                "BOSS TURN #$turnNumber: The Closet Tornado knotted ${candidateCells.size} cells on the board!"
            BossType.ULTIMATE_CHORE_DUO ->
                "BOSS TURN #$turnNumber: 92°F AC Heatwave + Messy Dish Pile struck ${candidateCells.size} cells!"
        }
        return updatedHazards to banner
    }

    fun activatePowerUp(type: PowerUpType) {
        val state = _boardState.value
        if (state.isLevelWon || state.isLevelLost) return
        val count = state.powerUpCounts[type] ?: 0
        if (count <= 0) return

        when (type) {
            PowerUpType.SPATULA_SNIP -> {
                val nowActive = !state.isSpatulaModeActive
                soundManager.playPowerUp()
                val updatedCounts = state.powerUpCounts.toMutableMap()
                if (nowActive) {
                    updatedCounts[type] = (count - 1).coerceAtLeast(0)
                }
                _boardState.update {
                    it.copy(
                        isSpatulaModeActive = nowActive,
                        powerUpCounts = updatedCounts,
                        activeSpeaker = CrewMember.SANDRA,
                        companionMessage = if (nowActive) {
                            "Golden Spatula Armed! Tap ANY arrow on the grid (even a blocked one!) to break the lock!"
                        } else {
                            "Golden Spatula put back on the rack."
                        }
                    )
                }
            }

            PowerUpType.YARN_BOMB -> {
                val clearArrows = state.remainingArrows.filter { candidate ->
                    LevelCatalog.findFirstBlockingCell(
                        arrow = candidate,
                        allActiveArrows = state.remainingArrows,
                        cols = state.levelConfig.gridCols,
                        rows = state.levelConfig.gridRows
                    ) == null
                }.take(3)

                if (clearArrows.isNotEmpty()) {
                    soundManager.playPowerUp()
                    val updatedCounts = state.powerUpCounts.toMutableMap()
                    updatedCounts[type] = (count - 1).coerceAtLeast(0)
                    _boardState.update { it.copy(powerUpCounts = updatedCounts) }
                    clearArrowsAndApplyTurn(
                        arrowsToClear = clearArrows,
                        usedSpatula = true
                    )
                }
            }

            PowerUpType.XRAY_SPECS -> {
                soundManager.playPowerUp()
                val peekIds = computeAllClearArrowIds(
                    state.remainingArrows,
                    state.levelConfig.gridCols,
                    state.levelConfig.gridRows
                ).take(2).toSet()
                val updatedCounts = state.powerUpCounts.toMutableMap()
                updatedCounts[type] = (count - 1).coerceAtLeast(0)
                _boardState.update {
                    it.copy(
                        xrayArrowIds = peekIds,
                        powerUpCounts = updatedCounts,
                        activeSpeaker = CrewMember.SANDRA,
                        companionMessage = "Sandra's X-Ray Specs Peek: Highlighting ${peekIds.size} keyhole opening${if (peekIds.size == 1) "" else "s"} for this move only!"
                    )
                }
            }

            PowerUpType.WARM_BRIOCHE -> {
                soundManager.playPowerUp()
                val updatedCounts = state.powerUpCounts.toMutableMap()
                updatedCounts[type] = (count - 1).coerceAtLeast(0)
                _boardState.update {
                    it.copy(
                        heartsRemaining = (it.heartsRemaining + 1).coerceAtMost(it.maxHearts),
                        hasBriocheShield = true,
                        powerUpCounts = updatedCounts,
                        activeSpeaker = CrewMember.NATY,
                        companionMessage = "Naty's Warm Brioche eaten! +1 Crochet Heart restored AND 1-Hit Bump Shield activated!"
                    )
                }
            }
        }
    }

    private fun computeAllClearArrowIds(
        arrows: List<PuzzleArrow>,
        cols: Int,
        rows: Int
    ): Set<Int> {
        return arrows.filter { candidate ->
            LevelCatalog.findFirstBlockingCell(
                arrow = candidate,
                allActiveArrows = arrows,
                cols = cols,
                rows = rows
            ) == null
        }.map { it.id }.toSet()
    }

    fun requestNatyHint() {
        val state = _boardState.value
        if (state.isLevelWon || state.isLevelLost) return

        if (state.hintsRemainingInLevel <= 0) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.NATY,
                    companionMessage = "Naty: You already used your 1 Tactical Hint for this level! Trace the arrow shafts carefully!"
                )
            }
            return
        }

        val clearArrow = state.remainingArrows.firstOrNull { candidate ->
            LevelCatalog.findFirstBlockingCell(
                arrow = candidate,
                allActiveArrows = state.remainingArrows,
                cols = state.levelConfig.gridCols,
                rows = state.levelConfig.gridRows
            ) == null
        }

        if (clearArrow != null) {
            soundManager.playPowerUp()
            val head = clearArrow.headCell
            _boardState.update {
                it.copy(
                    hintsRemainingInLevel = 0,
                    usedAssistInLevel = true,
                    hintedArrowId = clearArrow.id,
                    activeSpeaker = CrewMember.NATY,
                    companionMessage = "Naty's Tactical Hint (0 left, max 2⭐): Tap the ${clearArrow.yarnColor.displayName} arrow (${clearArrow.headDirection.symbol}) at row ${head.row + 1}, col ${head.col + 1}!"
                )
            }
        }
    }

    fun stitchExtraHeart() {
        val state = _boardState.value
        if (!state.emergencyStitchAvailable) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.SANDRA,
                    companionMessage = "Sandra: Emergency Stitch already used on this board! Tap 'Restart Board' to try a clean strategic run!"
                )
            }
            return
        }
        soundManager.playPowerUp()
        _boardState.update {
            it.copy(
                heartsRemaining = 1,
                emergencyStitchAvailable = false,
                usedEmergencyStitchInLevel = true,
                hasBriocheShield = true,
                isLevelLost = false,
                activeSpeaker = CrewMember.SANDRA,
                companionMessage = "Sandra stitched 1 Last-Chance Heart + Shield (Level capped at 1⭐)! Make every move count!"
            )
        }
    }

    fun advanceToNextLevel() {
        val current = _boardState.value.levelConfig
        when {
            current.isEndlessMode -> nextEndlessRound()
            current.isDailyChallenge || current.levelNumber >= 300 ->
                loadStoryLevel(progressState.value.highestUnlockedLevel)
            else -> {
                val next = if (current.levelNumber < LevelCatalog.totalStoryLevels) {
                    current.levelNumber + 1
                } else {
                    1
                }
                loadStoryLevel(next, allowJustUnlocked = next)
            }
        }
    }

    fun toggleRayGuide(enabled: Boolean) {
        val level = _boardState.value.levelConfig
        if (!level.allowRayGuide) {
            soundManager.playBlockedBump()
            _boardState.update {
                it.copy(
                    activeSpeaker = CrewMember.SANDRA,
                    companionMessage = "🔒 Pro Mode Active on Level ${level.levelNumber}: Free Ray Guide is disabled after Level 3! Trace lanes by eye!"
                )
            }
            return
        }
        if (enabled) {
            _boardState.update { it.copy(usedAssistInLevel = true) }
        }
        viewModelScope.launch {
            preferencesRepository.toggleRayGuide(enabled)
        }
    }

    fun toggleYarnStitchRibs(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.toggleYarnStitchRibs(enabled)
        }
    }

    fun toggleSfx(enabled: Boolean) {
        soundManager.setSoundEffectsEnabled(enabled)
        viewModelScope.launch {
            preferencesRepository.setSfxEnabled(enabled)
        }
    }

    fun toggleCalmingMusic(enabled: Boolean) {
        soundManager.setCalmingMusicEnabled(enabled)
        viewModelScope.launch {
            preferencesRepository.setCalmingMusicEnabled(enabled)
        }
    }

    fun setArrowPinStyle(style: ArrowPinStyle) {
        viewModelScope.launch {
            preferencesRepository.setArrowPinStyle(style)
        }
    }

    fun setBoardBgStyle(style: BoardBackgroundStyle) {
        viewModelScope.launch {
            preferencesRepository.setBoardBgStyle(style)
        }
    }

    fun setDarkThemeOverride(darkOverride: Boolean?) {
        viewModelScope.launch {
            preferencesRepository.setDarkThemeOverride(darkOverride)
        }
    }

    fun toggleCollectibleInStack(layerId: String) {
        viewModelScope.launch {
            preferencesRepository.toggleCollectibleInStack(
                layerId = layerId,
                currentList = progressState.value.activeBurgerStackIds
            )
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            preferencesRepository.resetAllProgress()
            val config = LevelCatalog.getStoryLevel(1)
            startLevelWithConfig(config, resetPowerUpsIfEmpty = true)
        }
    }

    private fun buildSuccessReaction(
        combo: Int,
        remainingCount: Int,
        openingsCount: Int,
        isWon: Boolean,
        starsEarned: Int,
        boss: BossType?,
        damageDealt: Int?,
        hilariousSound: HilariousArrowSound
    ): Pair<CrewMember, String> {
        if (isWon) {
            val starStr = "⭐".repeat(starsEarned)
            return if (boss != null) {
                CrewMember.SANDRA to "$starStr ${boss.victoryQuote} BRING HOME THE JERKY!!"
            } else {
                CrewMember.SANDRA to "$starStr BRING HOME THE JERKY!! Level mastered ($starsEarned/3 Stars)!"
            }
        }
        if (hilariousSound == HilariousArrowSound.BRING_HOME_THE_JERKY) {
            return CrewMember.SANDRA to "Sandra: 'BRING HOME THE JERKY!!' (${combo}x Combo! $remainingCount arrows left, $openingsCount clear)"
        }
        if (boss != null && damageDealt != null) {
            return CrewMember.SANDRA to "${hilariousSound.comicText} Struck ${boss.bossName} for -$damageDealt HP! ($remainingCount left • $openingsCount clear)"
        }
        return when {
            combo >= 4 -> CrewMember.ALLY to "Ally: ${hilariousSound.comicText} ${combo}x STRATEGIC COMBO! ($remainingCount left • $openingsCount keyhole${if (openingsCount == 1) "" else "s"} open)"
            else -> CrewMember.NATY to "Naty: ${hilariousSound.comicText} Clean launch! $remainingCount arrows left ($openingsCount currently unblocked)."
        }
    }

    private fun buildBlockedReaction(
        heartsLeft: Int,
        blockingCell: GridCell,
        bossHealed: Boolean
    ): Pair<CrewMember, String> {
        if (heartsLeft == 0) {
            return CrewMember.ALLY to "Ally: Out of Crochet Hearts! Restart the board for a 3-Star run, or use your 1 Emergency Stitch (caps at 1⭐)!"
        }
        val bossNote = if (bossHealed) " Boss repaired +20 HP!" else ""
        return CrewMember.NATY to "Naty: Blocked at row ${blockingCell.row + 1}, col ${blockingCell.col + 1}!$bossNote Trace the exit lane first ($heartsLeft Heart${if (heartsLeft == 1) "" else "s"} left)."
    }
}
