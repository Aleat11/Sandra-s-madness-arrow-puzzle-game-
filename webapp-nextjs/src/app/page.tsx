"use client";

import React, { useEffect, useMemo, useRef, useState } from "react";
import {
  ARROW_PIN_STYLES,
  ArrowPinStyleId,
  BOARD_BG_STYLES,
  BOSS_TYPES,
  BlockedArrowAnim,
  BoardBackgroundStyleId,
  BossTypeId,
  CREW_MEMBERS,
  CrewMemberId,
  ExitingArrowAnim,
  GridCell,
  LaunchExplosionAnim,
  LevelConfig,
  POWER_UPS,
  PowerUpTypeId,
  PuzzleArrow,
  cellKey
} from "@/types/game";
import {
  BURGER_COLLECTIBLES,
  DAILY_CHALLENGE_UNLOCK_LEVEL,
  TOTAL_STORY_LEVELS,
  countCurrentlyClearArrows,
  findFirstBlockingCell,
  generateEndlessMadnessLevel,
  getAllStoryLevels,
  getBossBattleLevel,
  getStoryLevel,
  isBossUnlocked,
  isDailyChallengeUnlocked,
  isEndlessSizeUnlocked,
  isStoryLevelUnlocked,
  requiredLevelForEndlessSize
} from "@/lib/levelCatalog";
import { HilariousSoundMeta, MUSIC_TRACKS, MusicTrackMeta, soundController } from "@/lib/soundManager";
import { ArrowBoardCanvas } from "@/components/ArrowBoardCanvas";
import { BossArenaCard } from "@/components/BossArenaCard";

type ActiveTab = "PLAY" | "CHAPTERS" | "KITCHEN" | "CREW";

const ARCHER_QUIPS = [
  "Bullseye! That arrow found its lane before the joke found its target.",
  "Straight and narrow, just like an arrow's preferred commute.",
  "One less arrow on the board. The quiver is looking less crowded already.",
  "Nice shot! You really know how to point yourself in the right direction.",
  "That launch was on point. The target never saw it coming.",
  "Arrow you ready for the next one? Because the board certainly isn't."
];

const BLOCKED_ARCHER_QUIPS = [
  "That shot hit traffic. Clear the lane before you let it fly!",
  "The arrow's got places to be, but something's in its way.",
  "No bullseye yet. Give that arrow a clear runway first.",
  "A blocked shot! Even arrows need right of way."
];

export default function SandrasMadnessWebApp() {
  const [activeTab, setActiveTab] = useState<ActiveTab>("PLAY");

  // Persistent Player Progression
  const [highestUnlockedLevel, setHighestUnlockedLevel] = useState<number>(1);
  const [completedLevels, setCompletedLevels] = useState<number[]>([]);
  const [levelStars, setLevelStars] = useState<Record<number, number>>({});
  const [totalScore, setTotalScore] = useState<number>(0);
  const [bossesDefeated, setBossesDefeated] = useState<number>(0);
  const [endlessRoundsWon, setEndlessRoundsWon] = useState<number>(0);
  const [dailyTrophies, setDailyTrophies] = useState<number>(0);

  // Visual & Audio Preferences
  const [arrowPinStyle, setArrowPinStyle] = useState<ArrowPinStyleId>("MINIMAL_NARROW");
  const [boardBgStyle, setBoardBgStyle] = useState<BoardBackgroundStyleId>("WAFFLE_LINEN");
  const [showRayGuide, setShowRayGuide] = useState<boolean>(false);
  const [showYarnRibs, setShowYarnRibs] = useState<boolean>(true);
  const [sfxEnabled, setSfxEnabled] = useState<boolean>(true);
  const [musicEnabled, setMusicEnabled] = useState<boolean>(true);
  const [isDarkTheme, setIsDarkTheme] = useState<boolean>(false);
  const [currentTrack, setCurrentTrack] = useState<MusicTrackMeta>(MUSIC_TRACKS[0]);

  // Active Puzzle Board State
  const [levelConfig, setLevelConfig] = useState<LevelConfig>(() => getStoryLevel(1));
  const [remainingArrows, setRemainingArrows] = useState<PuzzleArrow[]>(() => getStoryLevel(1).arrows);
  const [clearedCells, setClearedCells] = useState<Set<string>>(new Set());
  const [heartsRemaining, setHeartsRemaining] = useState<number>(3);
  const [maxHearts, setMaxHearts] = useState<number>(3);
  const [comboStreak, setComboStreak] = useState<number>(0);
  const [levelScore, setLevelScore] = useState<number>(0);
  const [movesMade, setMovesMade] = useState<number>(0);
  const [mistakesMade, setMistakesMade] = useState<number>(0);
  const [hintsRemaining, setHintsRemaining] = useState<number>(1);
  const [emergencyStitchAvailable, setEmergencyStitchAvailable] = useState<boolean>(true);
  const [usedAssist, setUsedAssist] = useState<boolean>(false);
  const [usedEmergencyStitch, setUsedEmergencyStitch] = useState<boolean>(false);
  const [earnedStarsOnWin, setEarnedStarsOnWin] = useState<number>(3);
  const [unlockedNextBanner, setUnlockedNextBanner] = useState<string | null>(null);

  const [hintedArrowId, setHintedArrowId] = useState<number | null>(null);
  const [xrayArrowIds, setXrayArrowIds] = useState<Set<number>>(new Set());
  const [isSpatulaArmed, setIsSpatulaArmed] = useState<boolean>(false);
  const [hasBriocheShield, setHasBriocheShield] = useState<boolean>(false);
  const [powerUpCounts, setPowerUpCounts] = useState<Record<PowerUpTypeId, number>>({
    SPATULA_SNIP: 1,
    YARN_BOMB: 1,
    XRAY_SPECS: 1,
    WARM_BRIOCHE: 1
  });

  // Animations & Boss State
  const [lastHilariousSound, setLastHilariousSound] = useState<HilariousSoundMeta | null>(null);
  const [launchExplosions, setLaunchExplosions] = useState<LaunchExplosionAnim[]>([]);
  const [exitingArrows, setExitingArrows] = useState<ExitingArrowAnim[]>([]);
  const [blockedAnim, setBlockedAnim] = useState<BlockedArrowAnim | null>(null);

  const [bossCurrentHp, setBossCurrentHp] = useState<number>(0);
  const [bossMaxHp, setBossMaxHp] = useState<number>(0);
  const [playerMovesLeftInTurn, setPlayerMovesLeftInTurn] = useState<number>(3);
  const [bossTurnCount, setBossTurnCount] = useState<number>(0);
  const [lastBossDamage, setLastBossDamage] = useState<number | null>(null);
  const [bossStatusBanner, setBossStatusBanner] = useState<string | null>(null);
  const [bossHazardCells, setBossHazardCells] = useState<Set<string>>(new Set());

  const [isLevelWon, setIsLevelWon] = useState<boolean>(false);
  const [isLevelLost, setIsLevelLost] = useState<boolean>(false);
  const [activeSpeaker, setActiveSpeaker] = useState<CrewMemberId>("SANDRA");
  const [companionMessage, setCompanionMessage] = useState<string>(getStoryLevel(1).hostTip);
  const [endlessGridSize, setEndlessGridSize] = useState<number>(5);
  const [endlessRound, setEndlessRound] = useState<number>(1);
  const [activeBurgerStack, setActiveBurgerStack] = useState<string[]>(["bottom_bun"]);

  const nextExplosionId = useRef<number>(1);

  useEffect(() => {
    soundController.initMusicLoop(track => setCurrentTrack(track));
    try {
      const raw = localStorage.getItem("sandras_madness_web_v1");
      if (raw) {
        const parsed = JSON.parse(raw);
        if (parsed.highestUnlockedLevel) setHighestUnlockedLevel(parsed.highestUnlockedLevel);
        if (Array.isArray(parsed.completedLevels)) setCompletedLevels(parsed.completedLevels);
        if (parsed.levelStars) setLevelStars(parsed.levelStars);
        if (typeof parsed.totalScore === "number") setTotalScore(parsed.totalScore);
      }
    } catch {
      // Ignore storage errors
    }
  }, []);

  const saveProgressToStorage = (
    nextHighest: number,
    nextCompleted: number[],
    nextStars: Record<number, number>,
    nextTotalScore: number
  ) => {
    try {
      localStorage.setItem(
        "sandras_madness_web_v1",
        JSON.stringify({
          highestUnlockedLevel: nextHighest,
          completedLevels: nextCompleted,
          levelStars: nextStars,
          totalScore: nextTotalScore
        })
      );
    } catch {
      // Ignore storage errors
    }
  };

  const totalStarsEarned = useMemo(
    () => Object.values(levelStars).reduce((acc, s) => acc + s, 0),
    [levelStars]
  );

  const clearKeyholeCount = useMemo(
    () => countCurrentlyClearArrows(remainingArrows, levelConfig.gridCols, levelConfig.gridRows),
    [remainingArrows, levelConfig.gridCols, levelConfig.gridRows]
  );

  const startLevelWithConfig = (config: LevelConfig) => {
    const boss = config.bossType ? BOSS_TYPES[config.bossType] : null;
    const initialHp = boss ? boss.maxHp : 0;
    const movesPerTurn = boss ? boss.playerMovesPerTurn : 3;
    const openings = countCurrentlyClearArrows(config.arrows, config.gridCols, config.gridRows);

    setActiveTab("PLAY");
    setLevelConfig(config);
    setRemainingArrows(config.arrows);
    setClearedCells(new Set());
    setHeartsRemaining(config.startingHearts);
    setMaxHearts(config.startingHearts);
    setComboStreak(0);
    setLevelScore(0);
    setMovesMade(0);
    setMistakesMade(0);
    setHintsRemaining(1);
    setEmergencyStitchAvailable(true);
    setUsedAssist(false);
    setUsedEmergencyStitch(false);
    setEarnedStarsOnWin(3);
    setUnlockedNextBanner(null);
    setHintedArrowId(null);
    setXrayArrowIds(new Set());
    setIsSpatulaArmed(false);
    setHasBriocheShield(false);
    setLaunchExplosions([]);
    setExitingArrows([]);
    setBlockedAnim(null);
    setBossCurrentHp(initialHp);
    setBossMaxHp(initialHp);
    setPlayerMovesLeftInTurn(movesPerTurn);
    setBossTurnCount(0);
    setLastBossDamage(null);
    setBossHazardCells(new Set());
    setBossStatusBanner(
      boss
        ? `Your Turn! ${openings} keyhole opening${openings === 1 ? "" : "s"} on board • ${movesPerTurn} launches before ${boss.bossName} counter-attacks!`
        : null
    );
    setIsLevelWon(false);
    setIsLevelLost(false);
    setActiveSpeaker(config.host);
    setCompanionMessage(`${config.hostTip} (${openings} keyhole arrow${openings === 1 ? " is" : "s are"} currently unblocked!)`);
  };

  const loadStoryLevel = (lvl: number, overrideUnlocked?: number) => {
    const unlockedCap = Math.max(highestUnlockedLevel, overrideUnlocked || 1);
    if (!isStoryLevelUnlocked(lvl, unlockedCap)) {
      soundController.playBlockedBump();
      setActiveSpeaker("SANDRA");
      setCompanionMessage(`🔒 Level ${lvl} is locked! Beat Level ${unlockedCap} first—you have to earn the higher levels!`);
      return;
    }
    startLevelWithConfig(getStoryLevel(lvl));
  };

  const handleCellTapped = (cell: GridCell) => {
    if (isLevelWon || isLevelLost) return;
    const tappedArrow = remainingArrows.find(a =>
      a.cells.some(c => c.col === cell.col && c.row === cell.row)
    );
    if (!tappedArrow) return;

    if (isSpatulaArmed) {
      clearArrowsAndApplyTurn([tappedArrow], true);
      return;
    }

    const blocker = findFirstBlockingCell(
      tappedArrow,
      remainingArrows,
      levelConfig.gridCols,
      levelConfig.gridRows
    );

    if (!blocker) {
      clearArrowsAndApplyTurn([tappedArrow], false);
    } else {
      handleBlockedTap(tappedArrow, blocker);
    }
  };

  const clearArrowsAndApplyTurn = (arrowsToClear: PuzzleArrow[], usedSpatula: boolean) => {
    const clearIds = new Set(arrowsToClear.map(a => a.id));
    const updatedRemaining = remainingArrows.filter(a => !clearIds.has(a.id));
    const nextCleared = new Set(clearedCells);
    arrowsToClear.forEach(a => a.cells.forEach(c => nextCleared.add(cellKey(c))));

    const newCombo = comboStreak + arrowsToClear.length;
    const pointsEarned = arrowsToClear.reduce((acc, a) => acc + a.cells.length * 55, 0) + newCombo * 35;
    const isNowWon = updatedRemaining.length === 0;

    const hilarious = soundController.playHilariousLaunch(newCombo);
    if (isNowWon) soundController.playVictory();
    setLastHilariousSound(hilarious);

    const nowMs = Date.now();
    const createdExplosions: LaunchExplosionAnim[] = arrowsToClear.map(arr => ({
      id: nextExplosionId.current++,
      cells: arr.cells,
      primaryColor: arr.yarnColor.primaryColor,
      highlightColor: arr.yarnColor.highlightColor,
      comicText: hilarious.comicText,
      comboCount: newCombo,
      headCell: arr.cells[arr.cells.length - 1],
      startTimeMs: nowMs
    }));

    setLaunchExplosions(prev => [...prev, ...createdExplosions]);
    setExitingArrows(prev => [...prev, ...arrowsToClear.map(arrow => ({ arrow, startTimeMs: nowMs }))]);

    setTimeout(() => {
      setExitingArrows(prev => prev.filter(item => !clearIds.has(item.arrow.id)));
    }, 450);
    setTimeout(() => {
      const expIds = new Set(createdExplosions.map(e => e.id));
      setLaunchExplosions(prev => prev.filter(e => !expIds.has(e.id)));
    }, 680);

    // Boss turn damage & counter-attack logic
    const boss = levelConfig.bossType ? BOSS_TYPES[levelConfig.bossType] : null;
    if (boss) {
      const rawDmg = isNowWon
        ? Math.max(45, bossCurrentHp)
        : Math.floor(bossMaxHp / Math.max(1, levelConfig.arrows.length)) + 12 + newCombo * 5 + (usedSpatula ? 20 : 0);
      const nextHp = isNowWon ? 0 : Math.max(0, bossCurrentHp - rawDmg);
      setLastBossDamage(rawDmg);
      setBossCurrentHp(nextHp);

      if (!isNowWon && nextHp > 0) {
        const nextMoves = playerMovesLeftInTurn - 1;
        if (nextMoves <= 0) {
          const nextTurn = bossTurnCount + 1;
          setBossTurnCount(nextTurn);
          setPlayerMovesLeftInTurn(boss.playerMovesPerTurn);
          const candidateCells = updatedRemaining
            .flatMap(a => a.cells)
            .slice(0, 3)
            .map(cellKey);
          setBossHazardCells(prev => new Set([...Array.from(prev), ...candidateCells]));
          setBossStatusBanner(
            `BOSS TURN #${nextTurn}: ${boss.bossName} dropped hazards onto ${candidateCells.length} cells!`
          );
        } else {
          setPlayerMovesLeftInTurn(nextMoves);
          setBossStatusBanner(
            `${hilarious.comicText} Direct Hit (-${rawDmg} HP)! ${nextMoves} launch${nextMoves === 1 ? "" : "es"} left in turn!`
          );
        }
      } else {
        setBossStatusBanner(boss.victoryQuote);
      }
    }

    // Power-up bonus every 8x combo
    const updatedPowerUps = { ...powerUpCounts };
    if (newCombo > 0 && newCombo % 8 === 0) {
      const keys: PowerUpTypeId[] = ["SPATULA_SNIP", "YARN_BOMB", "XRAY_SPECS", "WARM_BRIOCHE"];
      const bonusKey = keys[(newCombo / 8) % keys.length];
      updatedPowerUps[bonusKey] = (updatedPowerUps[bonusKey] || 0) + 1;
    }

    const stars = usedEmergencyStitch ? 1 : mistakesMade === 0 && !usedAssist ? 3 : mistakesMade <= 1 ? 2 : 1;
    setEarnedStarsOnWin(stars);

    if (isNowWon) {
      if (stars === 3) {
        updatedPowerUps.SPATULA_SNIP = (updatedPowerUps.SPATULA_SNIP || 0) + 1;
      }
      const nextTotalScore = totalScore + levelScore + pointsEarned;
      setTotalScore(nextTotalScore);
      if (boss) setBossesDefeated(prev => prev + 1);
      if (levelConfig.isEndlessMode) setEndlessRoundsWon(prev => prev + 1);
      if (levelConfig.isDailyChallenge) setDailyTrophies(prev => prev + 1);

      if (!levelConfig.isDailyChallenge && !levelConfig.isEndlessMode && levelConfig.levelNumber <= TOTAL_STORY_LEVELS) {
        const lvl = levelConfig.levelNumber;
        const nextCompleted = completedLevels.includes(lvl) ? completedLevels : [...completedLevels, lvl];
        const nextStars = { ...levelStars, [lvl]: Math.max(levelStars[lvl] || 0, stars) };
        const nextHighest = Math.min(TOTAL_STORY_LEVELS, Math.max(highestUnlockedLevel, lvl + 1));

        if (nextHighest > highestUnlockedLevel) {
          setUnlockedNextBanner(`🔓 UNLOCKED Level ${nextHighest}: ${getStoryLevel(nextHighest).title}!`);
        }
        setCompletedLevels(nextCompleted);
        setLevelStars(nextStars);
        setHighestUnlockedLevel(nextHighest);
        saveProgressToStorage(nextHighest, nextCompleted, nextStars, nextTotalScore);
      }
    }

    setRemainingArrows(updatedRemaining);
    setClearedCells(nextCleared);
    setComboStreak(newCombo);
    setLevelScore(prev => prev + pointsEarned);
    setMovesMade(prev => prev + 1);
    setHintedArrowId(null);
    setXrayArrowIds(new Set());
    setIsSpatulaArmed(false);
    setPowerUpCounts(updatedPowerUps);
    setIsLevelWon(isNowWon);
    setActiveSpeaker(isNowWon ? "SANDRA" : newCombo >= 4 ? "ALLY" : "NATY");
    setCompanionMessage(
      isNowWon
        ? `⭐ MOMMA'S BRINGIN' HOME THE JERKY!! Cleared with ${stars}/3 Stars!`
        : `${hilarious.comicText} ${newCombo}x Combo! ${updatedRemaining.length} arrows left.`
    );
  };

  const handleBlockedTap = (arrow: PuzzleArrow, blocker: GridCell) => {
    soundController.playBlockedBump();
    setBlockedAnim({ arrowId: arrow.id, blockingCell: blocker, timestampMs: Date.now() });
    setTimeout(() => setBlockedAnim(null), 650);

    if (hasBriocheShield) {
      setHasBriocheShield(false);
      setActiveSpeaker("NATY");
      setCompanionMessage(
        `Naty's Warm Brioche Shield absorbed the bump at row ${blocker.row + 1}, col ${blocker.col + 1}!`
      );
      return;
    }

    const isHazard = arrow.cells.some(c => bossHazardCells.has(cellKey(c)));
    const penalty = isHazard ? 2 : 1;
    const nextHearts = Math.max(0, heartsRemaining - penalty);
    const lost = nextHearts === 0;

    if (levelConfig.bossType && !lost) {
      setBossCurrentHp(prev => Math.min(bossMaxHp, prev + 20));
      setBossStatusBanner(`⚠️ Blocked mistake! ${BOSS_TYPES[levelConfig.bossType].bossName} repaired +20 HP!`);
    }

    setHeartsRemaining(nextHearts);
    setComboStreak(0);
    setMovesMade(prev => prev + 1);
    setMistakesMade(prev => prev + 1);
    setIsLevelLost(lost);
    setActiveSpeaker(lost ? "ALLY" : "NATY");
    setCompanionMessage(
      lost
        ? "Ally: Out of Crochet Hearts! Restart the board for a 3-Star run, or use your 1 Emergency Stitch!"
        : `Naty: Blocked at row ${blocker.row + 1}, col ${blocker.col + 1}! ${BLOCKED_ARCHER_QUIPS[movesMade % BLOCKED_ARCHER_QUIPS.length]} (${nextHearts} Heart${nextHearts === 1 ? "" : "s"} left).`
    );
  };

  const activatePowerUp = (type: PowerUpTypeId) => {
    if (isLevelWon || isLevelLost) return;
    const count = powerUpCounts[type] || 0;
    if (count <= 0) return;

    if (type === "SPATULA_SNIP") {
      const nextArmed = !isSpatulaArmed;
      setIsSpatulaArmed(nextArmed);
      if (nextArmed) {
        setPowerUpCounts(prev => ({ ...prev, SPATULA_SNIP: prev.SPATULA_SNIP - 1 }));
        setCompanionMessage("Sandra: Golden Spatula Armed! Click ANY arrow on the grid (even blocked!) to flip it off!");
      }
    } else if (type === "YARN_BOMB") {
      const clears = remainingArrows
        .filter(a => findFirstBlockingCell(a, remainingArrows, levelConfig.gridCols, levelConfig.gridRows) === null)
        .slice(0, 3);
      if (clears.length > 0) {
        setPowerUpCounts(prev => ({ ...prev, YARN_BOMB: prev.YARN_BOMB - 1 }));
        clearArrowsAndApplyTurn(clears, true);
      }
    } else if (type === "XRAY_SPECS") {
      const peek = remainingArrows
        .filter(a => findFirstBlockingCell(a, remainingArrows, levelConfig.gridCols, levelConfig.gridRows) === null)
        .slice(0, 2)
        .map(a => a.id);
      setPowerUpCounts(prev => ({ ...prev, XRAY_SPECS: prev.XRAY_SPECS - 1 }));
      setXrayArrowIds(new Set(peek));
      setCompanionMessage(`Sandra's X-Ray Specs: Highlighting ${peek.length} keyhole opening(s) for this move only!`);
    } else if (type === "WARM_BRIOCHE") {
      setPowerUpCounts(prev => ({ ...prev, WARM_BRIOCHE: prev.WARM_BRIOCHE - 1 }));
      setHeartsRemaining(prev => Math.min(maxHearts, prev + 1));
      setHasBriocheShield(true);
      setCompanionMessage("Naty's Warm Brioche eaten! +1 Heart restored AND 1-Hit Bump Shield active!");
    }
  };

  const requestHint = () => {
    if (hintsRemaining <= 0 || isLevelWon || isLevelLost) return;
    const clear = remainingArrows.find(
      a => findFirstBlockingCell(a, remainingArrows, levelConfig.gridCols, levelConfig.gridRows) === null
    );
    if (clear) {
      const head = clear.cells[clear.cells.length - 1];
      setHintsRemaining(0);
      setUsedAssist(true);
      setHintedArrowId(clear.id);
      setActiveSpeaker("NATY");
      setCompanionMessage(
        `Naty's Tactical Hint (0 left, max 2⭐): Click the ${clear.yarnColor.displayName} arrow at row ${head.row + 1}, col ${head.col + 1}!`
      );
    }
  };

  const useEmergencyStitch = () => {
    if (!emergencyStitchAvailable) return;
    setHeartsRemaining(1);
    setEmergencyStitchAvailable(false);
    setUsedEmergencyStitch(true);
    setHasBriocheShield(true);
    setIsLevelLost(false);
    setActiveSpeaker("SANDRA");
    setCompanionMessage("Sandra stitched 1 Last-Chance Heart + Shield (Level capped at 1⭐)! Make every move count!");
  };

  const allStoryLevels = useMemo(() => getAllStoryLevels(), []);

  return (
    <div className={isDarkTheme ? "dark" : ""}>
      <div className="app-shell min-h-screen flex flex-col items-center px-3 py-4 max-w-7xl mx-auto gap-3">
        {/* Top Header Card */}
        <header className="app-header w-full rounded-3xl bg-amber-100/80 dark:bg-stone-900 border border-amber-300/80 dark:border-stone-700 p-3.5 shadow-sm">
          <div className="flex items-center justify-between gap-2 flex-wrap">
            <div className="flex items-center gap-3">
              <img
                src="/assets/sandra-photo.jpg"
                alt="Sandra"
                className="w-12 h-12 rounded-full object-cover border-2 border-sandra-terracotta shadow"
              />
              <div>
                <h1 className="text-2xl font-extrabold text-sandra-terracotta leading-tight tracking-tight">
                  Sandra&apos;s Madness: Naty &amp; Ally
                </h1>
                <p className="text-xs font-semibold text-amber-900 dark:text-amber-200">
                  Lv.{highestUnlockedLevel}/{TOTAL_STORY_LEVELS} Unlocked • ⭐{totalStarsEarned} • 🎵 {currentTrack.title}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-1.5">
              <button
                onClick={() => {
                  const s = soundController.speakBringHomeTheJerky();
                  setLastHilariousSound(s);
                  setActiveSpeaker("SANDRA");
                  setCompanionMessage("Sandra shouts: 'MOMMA'S BRINGIN' HOME THE JERKY!!' 🥩🔥");
                }}
                className="px-3 py-1.5 rounded-full bg-red-600 text-white text-xs font-bold shadow hover:bg-red-700"
              >
                🥩 Momma's Jerky!
              </button>
              <button
                onClick={() => {
                  const next = !sfxEnabled;
                  setSfxEnabled(next);
                  soundController.setSfxEnabled(next);
                }}
                className="px-2.5 py-1.5 rounded-full bg-white dark:bg-stone-800 border text-xs font-bold"
              >
                {sfxEnabled ? "🔊 SFX" : "🔇 SFX"}
              </button>
              <button
                onClick={() => {
                  const next = !musicEnabled;
                  setMusicEnabled(next);
                  soundController.setMusicEnabled(next);
                }}
                className="px-2.5 py-1.5 rounded-full bg-white dark:bg-stone-800 border text-xs font-bold"
              >
                {musicEnabled ? "🎵 Music" : "🔕 Mute"}
              </button>
              <button
                onClick={() => {
                  const next = soundController.switchToNextSong();
                  setCurrentTrack(next);
                }}
                className="px-2.5 py-1.5 rounded-full bg-sky-100 text-sky-900 text-xs font-bold"
              >
                ⏭️ Song
              </button>
            </div>
          </div>

          {/* Score & Quick Boss Chips */}
          <div className="flex items-center justify-between gap-2 mt-2.5 flex-wrap">
            <div className="flex items-center gap-2 px-3 py-1 rounded-full bg-amber-500/20 border border-amber-500 text-xs font-extrabold">
              <span>🍔 Score: {levelScore}</span>
              {comboStreak >= 2 && <span className="text-red-600">🔥 {comboStreak}x COMBO</span>}
              {hasBriocheShield && <span className="text-sky-600">🛡️ Shield</span>}
            </div>

            <div className="flex items-center gap-1.5">
              {(["MESSY_HOUSE", "BROKEN_AC"] as BossTypeId[]).map(bossId => {
                const unlocked = isBossUnlocked(bossId, highestUnlockedLevel, completedLevels);
                const b = BOSS_TYPES[bossId];
                return (
                  <button
                    key={bossId}
                    onClick={() => {
                      if (!unlocked) {
                        soundController.playBlockedBump();
                        setCompanionMessage(`🔒 ${b.bossName} unlocks at Story Level ${b.unlockLevel}!`);
                        return;
                      }
                      startLevelWithConfig(getBossBattleLevel(bossId));
                    }}
                    className="px-2.5 py-1 rounded-full text-xs font-bold border bg-white dark:bg-stone-800"
                  >
                    {unlocked
                      ? bossId === "MESSY_HOUSE"
                        ? "🏠 Messy House"
                        : "❄️ Broken AC"
                      : `🔒 Lv.${b.unlockLevel}`}
                  </button>
                );
              })}
            </div>
          </div>
        </header>

        {/* Main View Switcher */}
        {activeTab === "PLAY" && (
          <main className="game-main w-full flex flex-col gap-2.5 items-center">
            {levelConfig.bossType && (
              <BossArenaCard
                bossType={levelConfig.bossType}
                currentHp={bossCurrentHp}
                maxHp={bossMaxHp}
                movesLeftInTurn={playerMovesLeftInTurn}
                lastDamage={lastBossDamage}
                statusBanner={bossStatusBanner}
              />
            )}

            {/* Level Info & Hearts Bar */}
            <div className="powerup-panel w-full rounded-2xl bg-white dark:bg-stone-900 border border-amber-200 dark:border-stone-700 p-3 shadow-sm">
              <div className="flex items-center justify-between">
                <button
                  disabled={levelConfig.levelNumber <= 1 || levelConfig.levelNumber > TOTAL_STORY_LEVELS}
                  onClick={() => loadStoryLevel(levelConfig.levelNumber - 1)}
                  className="px-3 py-1 rounded-full border text-sm font-bold disabled:opacity-30"
                >
                  ◀
                </button>
                <div className="text-center">
                  <span className="text-[11px] font-bold uppercase px-2.5 py-0.5 rounded-full bg-amber-100 text-amber-900">
                    {levelConfig.chapterName} • {levelConfig.accentBadge}
                  </span>
                  <h2 className="text-base font-extrabold mt-1">
                    {levelConfig.levelNumber <= TOTAL_STORY_LEVELS
                      ? `Level ${levelConfig.levelNumber}: ${levelConfig.title}`
                      : levelConfig.title}
                  </h2>
                  <p className="text-xs opacity-75">
                    {levelConfig.shapeName} ({levelConfig.gridCols}×{levelConfig.gridRows} • Par {levelConfig.parMoves} Moves)
                  </p>
                </div>
                <button
                  disabled={levelConfig.levelNumber >= TOTAL_STORY_LEVELS}
                  onClick={() => loadStoryLevel(levelConfig.levelNumber + 1)}
                  className="px-3 py-1 rounded-full border text-sm font-bold disabled:opacity-30"
                >
                  {isStoryLevelUnlocked(levelConfig.levelNumber + 1, highestUnlockedLevel) ? "▶" : "🔒"}
                </button>
              </div>

              <div className="flex items-center justify-between mt-2.5 pt-2 border-t border-amber-100 dark:border-stone-800 text-xs font-bold">
                <div>
                  {"❤️".repeat(heartsRemaining)}
                  {"🖤".repeat(Math.max(0, maxHearts - heartsRemaining))} {heartsRemaining}/{maxHearts}
                </div>
                <div className="px-2.5 py-1 rounded-full bg-amber-50 dark:bg-stone-800">
                  Left: {remainingArrows.length} • Keyholes Open: {clearKeyholeCount}
                </div>
                <button
                  onClick={() => {
                    if (!levelConfig.allowRayGuide) {
                      soundController.playBlockedBump();
                      setCompanionMessage("🔒 Pro Mode Active: Free Ray Guide is disabled after Level 3!");
                      return;
                    }
                    setShowRayGuide(!showRayGuide);
                    setUsedAssist(true);
                  }}
                  className="px-2.5 py-1 rounded-full border"
                >
                  {levelConfig.allowRayGuide
                    ? showRayGuide
                      ? "👁️ Guide: ON"
                      : "👁️ Ray Guide"
                    : "🔒 Pro Mode"}
                </button>
              </div>
            </div>

            {/* Companion Message Strip */}
            <div className="companion-strip w-full rounded-2xl bg-amber-50/90 dark:bg-stone-900 border border-amber-200 dark:border-stone-700 px-3.5 py-2.5 flex items-center justify-between gap-3">
              <div className="text-xs">
                <span className="font-extrabold" style={{ color: CREW_MEMBERS[activeSpeaker].badgeColor }}>
                  {CREW_MEMBERS[activeSpeaker].displayName}:{" "}
                </span>
                <span>{companionMessage}</span>
              </div>
              {lastHilariousSound && (
                <span
                  className="px-2.5 py-1 rounded-full text-white text-[11px] font-extrabold shrink-0"
                  style={{ backgroundColor: lastHilariousSound.badgeColor }}
                >
                  {lastHilariousSound.comicText}
                </span>
              )}
            </div>

            {/* Main Interactive Board + Overlays */}
            <div className="game-board-wrap relative w-full flex justify-center">
              <ArrowBoardCanvas
                gridCols={levelConfig.gridCols}
                gridRows={levelConfig.gridRows}
                arrows={remainingArrows}
                exitingArrows={exitingArrows}
                launchExplosions={launchExplosions}
                clearedCells={clearedCells}
                bossHazardCells={bossHazardCells}
                hintedArrowId={hintedArrowId}
                xrayArrowIds={xrayArrowIds}
                isSpatulaModeActive={isSpatulaArmed}
                blockedAnim={blockedAnim}
                showRayGuide={levelConfig.allowRayGuide && showRayGuide}
                showYarnStitchRibs={showYarnRibs}
                arrowPinStyle={arrowPinStyle}
                boardBgStyle={boardBgStyle}
                isDarkTheme={isDarkTheme}
                onCellTapped={handleCellTapped}
              />

              {isLevelWon && (
                <div className="absolute inset-0 bg-black/50 rounded-3xl flex items-center justify-center p-4">
                  <div className="bg-white dark:bg-stone-900 rounded-3xl p-6 max-w-sm w-full text-center border-2 border-sandra-terracotta shadow-2xl">
                    <div className="text-3xl mb-2">{"⭐".repeat(earnedStarsOnWin)}</div>
                    <h3 className="text-xl font-extrabold text-sandra-terracotta">
                      MOMMA'S BRINGIN' HOME THE JERKY!! 🥩
                    </h3>
                    <p className="text-xs mt-1 opacity-80">
                      Cleared &apos;{levelConfig.title}&apos; with {heartsRemaining}/{maxHearts} Hearts ({earnedStarsOnWin}/3 Stars)!
                    </p>
                    {unlockedNextBanner && (
                      <div className="mt-2 px-3 py-1 rounded-full bg-amber-100 text-amber-900 text-xs font-bold">
                        {unlockedNextBanner}
                      </div>
                    )}
                    <div className="flex gap-2 justify-center mt-4">
                      <button
                        onClick={() => startLevelWithConfig(levelConfig)}
                        className="px-4 py-2 rounded-full border font-bold text-xs"
                      >
                        Replay
                      </button>
                      <button
                        onClick={() => {
                          if (levelConfig.isEndlessMode) {
                            const nextR = endlessRound + 1;
                            setEndlessRound(nextR);
                            startLevelWithConfig(generateEndlessMadnessLevel(endlessGridSize, nextR, Date.now()));
                          } else {
                            const nextLvl = Math.min(TOTAL_STORY_LEVELS, levelConfig.levelNumber + 1);
                            loadStoryLevel(nextLvl, nextLvl);
                          }
                        }}
                        className="px-4 py-2 rounded-full bg-sandra-terracotta text-white font-bold text-xs"
                      >
                        Next Earned Level ▶
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {isLevelLost && (
                <div className="absolute inset-0 bg-black/50 rounded-3xl flex items-center justify-center p-4">
                  <div className="bg-white dark:bg-stone-900 rounded-3xl p-6 max-w-sm w-full text-center border-2 border-red-600 shadow-2xl">
                    <h3 className="text-xl font-extrabold text-red-600">Yarn Locked &amp; Tangled!</h3>
                    <p className="text-xs mt-2 opacity-85">
                      You ran out of Crochet Hearts! Restart the board for a full 3-Star mastery run, or use your 1 Emergency Stitch (caps level at 1⭐).
                    </p>
                    <div className="flex gap-2 justify-center mt-4">
                      <button
                        onClick={() => startLevelWithConfig(levelConfig)}
                        className="px-4 py-2 rounded-full border font-bold text-xs"
                      >
                        Restart Board
                      </button>
                      {emergencyStitchAvailable && (
                        <button
                          onClick={useEmergencyStitch}
                          className="px-4 py-2 rounded-full bg-red-600 text-white font-bold text-xs"
                        >
                          1x Stitch (1⭐ Max)
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>

            {/* Scarce Earned Power-Ups Bar */}
            <div className="w-full rounded-2xl bg-white dark:bg-stone-900 border border-amber-200 dark:border-stone-700 p-3 shadow-sm">
              <p className="text-[11px] font-bold opacity-70 mb-2">
                Earned Power-Up Stash (Earn +1 charge for 3⭐ Flawless Clears or 8x Combos!)
              </p>
              <div className="grid grid-cols-4 gap-2">
                {POWER_UPS.map(pu => {
                  const count = powerUpCounts[pu.id] || 0;
                  const armed = pu.id === "SPATULA_SNIP" && isSpatulaArmed;
                  return (
                    <button
                      key={pu.id}
                      disabled={count <= 0 && !armed}
                      onClick={() => activatePowerUp(pu.id)}
                      className={`p-2 rounded-xl border text-center transition ${
                        armed ? "bg-amber-500 text-white font-extrabold" : "bg-amber-50/60 dark:bg-stone-800"
                      } disabled:opacity-40`}
                    >
                      <div className="text-xs font-extrabold">
                        {pu.shortLabel} ×{count}
                      </div>
                    </button>
                  );
                })}
              </div>

              <div className="flex flex-wrap gap-2 justify-center mt-3">
                <button
                  onClick={() => {
                    const s = soundController.speakBringHomeTheJerky();
                    setLastHilariousSound(s);
                  }}
                  className="px-3 py-1.5 rounded-full bg-red-100 text-red-900 text-xs font-bold"
                >
                  🥩 Momma's Bringin' Home The Jerky!
                </button>
                <button
                  disabled={hintsRemaining <= 0}
                  onClick={requestHint}
                  className="px-3 py-1.5 rounded-full bg-sky-100 text-sky-900 text-xs font-bold disabled:opacity-40"
                >
                  💡 Hint ({hintsRemaining}/1)
                </button>
                <button
                  onClick={() => startLevelWithConfig(levelConfig)}
                  className="px-3 py-1.5 rounded-full border text-xs font-bold"
                >
                  🔄 Reset Board
                </button>
              </div>
            </div>
          </main>
        )}

        {/* CHAPTERS & BOSSES TAB */}
        {activeTab === "CHAPTERS" && (
          <main className="w-full flex flex-col gap-3">
            <div className="rounded-2xl bg-white dark:bg-stone-900 border p-4">
              <h2 className="text-lg font-extrabold text-sandra-terracotta">
                Progressive Story Chapters (1–20)
              </h2>
              <p className="text-xs opacity-75 mb-3">
                Beat each level to unlock the next! Every 5th level is a Turn-Based Boss Gate.
              </p>
              <div className="flex flex-col gap-2">
                {allStoryLevels.map(lvl => {
                  const unlocked = isStoryLevelUnlocked(lvl.levelNumber, highestUnlockedLevel);
                  const stars = levelStars[lvl.levelNumber] || 0;
                  return (
                    <div
                      key={lvl.levelNumber}
                      className={`p-3 rounded-xl border flex items-center justify-between ${
                        unlocked ? "bg-amber-50/50 dark:bg-stone-800" : "opacity-55 bg-stone-100 dark:bg-stone-900"
                      }`}
                    >
                      <div>
                        <div className="font-bold text-sm">
                          Level {lvl.levelNumber}: {lvl.title}
                        </div>
                        <div className="text-xs opacity-75">
                          {unlocked
                            ? `${lvl.shapeName} • ${lvl.gridCols}×${lvl.gridRows} • ${lvl.arrows.length} Arrows • ${lvl.startingHearts} Hearts`
                            : `🔒 Beat Level ${lvl.levelNumber - 1} to unlock`}
                        </div>
                      </div>
                      <div className="flex items-center gap-2">
                        {stars > 0 && <span className="text-xs">{"⭐".repeat(stars)}</span>}
                        <button
                          disabled={!unlocked}
                          onClick={() => loadStoryLevel(lvl.levelNumber)}
                          className="px-3 py-1.5 rounded-full bg-sandra-terracotta text-white text-xs font-bold disabled:bg-stone-400"
                        >
                          {unlocked ? "Play" : "🔒"}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Endless 5x5 to 10x10 Section */}
            <div className="rounded-2xl bg-white dark:bg-stone-900 border p-4">
              <h3 className="text-base font-extrabold">
                Sandra&apos;s Endless Madness (Earn up to 10×10 Grids!)
              </h3>
              <div className="flex flex-wrap gap-2 mt-2">
                {[5, 6, 7, 8, 9, 10].map(sz => {
                  const unlocked = isEndlessSizeUnlocked(sz, highestUnlockedLevel);
                  const req = requiredLevelForEndlessSize(sz);
                  return (
                    <button
                      key={sz}
                      disabled={!unlocked}
                      onClick={() => {
                        setEndlessGridSize(sz);
                        startLevelWithConfig(generateEndlessMadnessLevel(sz, endlessRound, Date.now()));
                      }}
                      className="px-3 py-1.5 rounded-full border text-xs font-bold disabled:opacity-45"
                    >
                      {unlocked ? `Play ${sz}×${sz}` : `🔒 ${sz}×${sz} (Lv.${req})`}
                    </button>
                  );
                })}
              </div>
            </div>
          </main>
        )}

        {/* KITCHEN & ASSETS TAB */}
        {activeTab === "KITCHEN" && (
          <main className="w-full flex flex-col gap-3">
            <div className="rounded-2xl bg-white dark:bg-stone-900 border p-4">
              <h2 className="text-lg font-extrabold text-sandra-terracotta">
                Custom Arrow Pins &amp; Crochet Mats
              </h2>
              <div className="mt-3">
                <p className="text-xs font-bold mb-1.5">Narrow Arrow / Pin Style:</p>
                <div className="flex flex-wrap gap-2">
                  {ARROW_PIN_STYLES.map(st => (
                    <button
                      key={st.id}
                      onClick={() => setArrowPinStyle(st.id)}
                      className={`px-3 py-1.5 rounded-full text-xs font-bold border ${
                        arrowPinStyle === st.id ? "bg-sandra-terracotta text-white" : ""
                      }`}
                    >
                      {st.title}
                    </button>
                  ))}
                </div>
              </div>

              <div className="mt-3">
                <p className="text-xs font-bold mb-1.5">Crochet Board Background Mat:</p>
                <div className="flex flex-wrap gap-2">
                  {BOARD_BG_STYLES.map(bg => (
                    <button
                      key={bg.id}
                      onClick={() => setBoardBgStyle(bg.id)}
                      className={`px-3 py-1.5 rounded-full text-xs font-bold border ${
                        boardBgStyle === bg.id ? "bg-amber-600 text-white" : ""
                      }`}
                    >
                      {bg.title}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            <div className="rounded-2xl bg-white dark:bg-stone-900 border p-4">
              <h3 className="text-base font-extrabold">
                Earned Burger, Reading &amp; Baking Collectibles ({activeBurgerStack.length} stacked)
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 mt-2">
                {BURGER_COLLECTIBLES.map(layer => {
                  const unlocked = highestUnlockedLevel >= layer.unlockedAtLevel;
                  return (
                    <div
                      key={layer.id}
                      className={`p-3 rounded-xl border ${unlocked ? "" : "opacity-50"}`}
                    >
                      <div className="font-bold text-xs">{layer.name}</div>
                      <div className="text-[11px] opacity-75">{layer.description}</div>
                      <div className="text-[10px] font-bold mt-1 text-amber-700">
                        {unlocked ? `✅ Unlocked (Lv.${layer.unlockedAtLevel})` : `🔒 Unlocks at Lv.${layer.unlockedAtLevel}`}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </main>
        )}

        {/* CREW NOOK TAB */}
        {activeTab === "CREW" && (
          <main className="w-full flex flex-col gap-3">
            {(["SANDRA", "NATY", "ALLY"] as CrewMemberId[]).map(id => {
              const member = CREW_MEMBERS[id];
              return (
                <div key={id} className="rounded-2xl bg-white dark:bg-stone-900 border p-4 flex gap-3 items-center">
                  {id === "SANDRA" ? (
                    <img
                      src="/assets/sandra-photo.jpg"
                      alt="Sandra"
                      className="w-16 h-16 rounded-full object-cover border-2 border-sandra-terracotta shrink-0"
                    />
                  ) : (
                    <div
                      className="w-16 h-16 rounded-full flex items-center justify-center text-white font-extrabold text-xl shrink-0"
                      style={{ backgroundColor: member.badgeColor }}
                    >
                      {member.displayName[0]}
                    </div>
                  )}
                  <div>
                    <h3 className="font-extrabold text-base" style={{ color: member.badgeColor }}>
                      {member.displayName} — {member.roleTitle}
                    </h3>
                    <p className="text-xs opacity-85 mt-1">{member.bio}</p>
                    <p className="text-[11px] font-bold mt-1 opacity-75">Specialty: {member.specialty}</p>
                  </div>
                </div>
              );
            })}
          </main>
        )}

        {/* Bottom Navigation Bar */}
        <nav className="w-full rounded-full bg-white dark:bg-stone-900 border-2 border-amber-200 dark:border-stone-700 p-1.5 flex justify-around shadow-md sticky bottom-2">
          {(
            [
              { id: "PLAY", label: "🎯 Play" },
              { id: "CHAPTERS", label: "📚 Chapters & Bosses" },
              { id: "KITCHEN", label: "🍔 Burger Assets" },
              { id: "CREW", label: "🧶 Sandra, Naty & Ally" }
            ] as Array<{ id: ActiveTab; label: string }>
          ).map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-3.5 py-2 rounded-full text-xs font-extrabold transition ${
                activeTab === tab.id ? "bg-sandra-terracotta text-white" : "hover:bg-amber-50 dark:hover:bg-stone-800"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>
    </div>
  );
}
