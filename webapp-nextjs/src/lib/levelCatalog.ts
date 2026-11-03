import {
  BOSS_TYPES,
  BossTypeId,
  BurgerYarnLayer,
  CrewMemberId,
  DIRECTIONS,
  DirectionKey,
  GridCell,
  LevelConfig,
  PuzzleArrow,
  YARN_PALETTE,
  cellKey,
  oppositeDirection,
  stepCell
} from "@/types/game";

export const DAILY_CHALLENGE_UNLOCK_LEVEL = 8;

export function isStoryLevelUnlocked(levelNumber: number, highestUnlockedLevel: number): boolean {
  return levelNumber <= Math.max(1, highestUnlockedLevel);
}

export function isBossUnlocked(
  bossId: BossTypeId,
  highestUnlockedLevel: number,
  completedLevels: number[]
): boolean {
  const unlock = BOSS_TYPES[bossId].unlockLevel;
  return unlock <= highestUnlockedLevel || completedLevels.includes(unlock);
}

export function requiredLevelForEndlessSize(gridSize: number): number {
  switch (gridSize) {
    case 5: return 3;
    case 6: return 6;
    case 7: return 10;
    case 8: return 13;
    case 9: return 16;
    default: return 19;
  }
}

export function isEndlessSizeUnlocked(gridSize: number, highestUnlockedLevel: number): boolean {
  return highestUnlockedLevel >= requiredLevelForEndlessSize(gridSize);
}

export function isDailyChallengeUnlocked(highestUnlockedLevel: number): boolean {
  return highestUnlockedLevel >= DAILY_CHALLENGE_UNLOCK_LEVEL;
}

export const BURGER_COLLECTIBLES: BurgerYarnLayer[] = [
  {
    id: "bottom_bun",
    name: "Oven-Baked Brioche Yarn Base",
    stitchCount: 128,
    unlockedAtLevel: 1,
    craftedBy: "NATY",
    category: "Crochet Burger",
    description: "Fresh from Naty's warm oven and double-crocheted in golden brioche yarn.",
    primaryColor: "#E59838",
    accentColor: "#FFF3D6"
  },
  {
    id: "recipe_storybook",
    name: "Sandra's Hardcover Recipe & Yarn Tome",
    stitchCount: 180,
    unlockedAtLevel: 2,
    craftedBy: "SANDRA",
    category: "Cozy Reading",
    description: "A cozy stitched hardcover novel & secret burger recipe book with a tasseled red yarn bookmark.",
    primaryColor: "#7C3AED",
    accentColor: "#FDE68A"
  },
  {
    id: "smash_patty",
    name: "Sizzling Amigurumi Smash Patty",
    unlockedAtLevel: 3,
    stitchCount: 210,
    craftedBy: "SANDRA",
    category: "Crochet Burger",
    description: "Textured waffle-stitch espresso yarn patty with caramelized char-loop edges.",
    primaryColor: "#6B3418",
    accentColor: "#9C522B"
  },
  {
    id: "baked_croissant",
    name: "Golden Flaky Yarn Croissant",
    unlockedAtLevel: 4,
    stitchCount: 190,
    craftedBy: "NATY",
    category: "Sweet Baking",
    description: "Buttery spiral-ribbed pastry baked to perfection for Sandra's morning reading nook.",
    primaryColor: "#F59E0B",
    accentColor: "#FEF3C7"
  },
  {
    id: "cheddar_melt",
    name: "Melted Cheddar Corners",
    unlockedAtLevel: 5,
    stitchCount: 164,
    craftedBy: "NATY",
    category: "Crochet Burger",
    description: "Bright goldenrod yarn stitched with drooping triangular cheese corners.",
    primaryColor: "#FBBF24",
    accentColor: "#F59E0B"
  },
  {
    id: "wavy_lettuce",
    name: "Ruffled Crochet Lettuce",
    unlockedAtLevel: 7,
    stitchCount: 195,
    craftedBy: "ALLY",
    category: "Crochet Burger",
    description: "Hyperbolic ruffle-stitch emerald yarn that gives every burger a cozy crunch.",
    primaryColor: "#34D399",
    accentColor: "#10B981"
  },
  {
    id: "frosted_cupcake",
    name: "Ally's Berry Swirl Baked Cupcake",
    unlockedAtLevel: 9,
    stitchCount: 225,
    craftedBy: "ALLY",
    category: "Sweet Baking",
    description: "Piped strawberry buttercream yarn swirls with rainbow stitched sprinkles and a cherry.",
    primaryColor: "#EC4899",
    accentColor: "#FCE7F3"
  },
  {
    id: "ruby_tomato",
    name: "Ruby Tomato Wheel",
    unlockedAtLevel: 11,
    stitchCount: 150,
    craftedBy: "NATY",
    category: "Crochet Burger",
    description: "Crimson magic-ring tomato slice with radial seed-pocket crochet spokes.",
    primaryColor: "#EF4444",
    accentColor: "#FCA5A5"
  },
  {
    id: "crinkle_pickles",
    name: "Crinkle-Stitch Dill Pickles",
    unlockedAtLevel: 13,
    stitchCount: 112,
    craftedBy: "ALLY",
    category: "Crochet Burger",
    description: "Three ribbed forest-green pickle medallions with lime yarn border ridges.",
    primaryColor: "#22C55E",
    accentColor: "#BBF7D0"
  },
  {
    id: "whisk_bookmark",
    name: "Baker's Whisk & Reading Glasses Charm",
    unlockedAtLevel: 15,
    stitchCount: 240,
    craftedBy: "SANDRA",
    category: "Cozy Reading",
    description: "Silver-thread pastry whisk paired with Sandra's cozy tortoiseshell reading glasses bookmark.",
    primaryColor: "#0EA5E9",
    accentColor: "#E0F2FE"
  },
  {
    id: "secret_sauce",
    name: "Ally's Madness Glaze & Sauce",
    unlockedAtLevel: 18,
    stitchCount: 175,
    craftedBy: "ALLY",
    category: "Crochet Burger",
    description: "Tangy coral-pink chain-stitch burger sauce blended with sweet bakery glaze.",
    primaryColor: "#F43F5E",
    accentColor: "#FECDD3"
  },
  {
    id: "sesame_crown",
    name: "Golden Baked Sesame Crown Bun",
    unlockedAtLevel: 20,
    stitchCount: 290,
    craftedBy: "SANDRA",
    category: "Crochet Burger",
    description: "Plush oven-browned dome top bun embroidered with French-knot sesame seeds.",
    primaryColor: "#D97706",
    accentColor: "#FEF3C7"
  }
];

interface LevelMeta {
  levelNumber: number;
  title: string;
  chapterName: string;
  shapeName: string;
  accentBadge: string;
  host: CrewMemberId;
  hostTip: string;
  cols: number;
  rows: number;
  mask: string[];
  difficultyStars: number;
  difficultyLabel: string;
  startingHearts: number;
  allowRayGuide: boolean;
  maxInitialOpenings: number;
  bossType?: BossTypeId;
}

const STORY_LEVEL_METAS: LevelMeta[] = [
  {
    levelNumber: 1,
    title: "Warm Brioche Keyhole",
    chapterName: "Ch. 1 • Cozy Book & Yarn Cafe",
    shapeName: "5×5 Baked Crochet Bun",
    accentBadge: "★☆☆☆☆ Apprentice",
    host: "SANDRA",
    hostTip: "Welcome to Sandra's Madness! Only 2 keyhole arrows are unblocked right now—trace each arrow's lane before clicking to unlock Level 2!",
    cols: 5,
    rows: 5,
    difficultyStars: 1,
    difficultyLabel: "Apprentice",
    startingHearts: 3,
    allowRayGuide: true,
    maxInitialOpenings: 2,
    mask: [".XXX.", "XXXXX", "XXXXX", "XXXXX", ".XXX."]
  },
  {
    levelNumber: 2,
    title: "Hardcover Spine Lock",
    chapterName: "Ch. 1 • Cozy Book & Yarn Cafe",
    shapeName: "5×5 Interlocking Book",
    accentBadge: "★★☆☆☆ Tactical",
    host: "SANDRA",
    hostTip: "Arrows bend around corners to block adjacent lanes! Find the 2 unblocked arrows to peel open the book spine.",
    cols: 5,
    rows: 5,
    difficultyStars: 2,
    difficultyLabel: "Tactical",
    startingHearts: 3,
    allowRayGuide: true,
    maxInitialOpenings: 2,
    mask: ["XXXXX", "XXXXX", "XX.XX", "XXXXX", ".XXX."]
  },
  {
    levelNumber: 3,
    title: "Sizzling 6×6 Smash Weave",
    chapterName: "Ch. 1 • Cozy Book & Yarn Cafe",
    shapeName: "6×6 Char-Loop Patty",
    accentBadge: "★★☆☆☆ Tactical",
    host: "NATY",
    hostTip: "Stepping up to a 6×6 grid! Last level with Apprentice Ray Guide—spot how L-shaped tails block cross-traffic!",
    cols: 6,
    rows: 6,
    difficultyStars: 2,
    difficultyLabel: "Tactical",
    startingHearts: 3,
    allowRayGuide: true,
    maxInitialOpenings: 2,
    mask: [".XXXX.", "XXXXXX", "XX..XX", "XX..XX", "XXXXXX", ".XXXX."]
  },
  {
    levelNumber: 4,
    title: "Naty's Braided Croissant Knot",
    chapterName: "Ch. 1 • Cozy Book & Yarn Cafe",
    shapeName: "6×6 Braided Pastry",
    accentBadge: "★★★☆☆ Challenging",
    host: "NATY",
    hostTip: "PRO RULES UNLOCKED: Free Ray Guide is now off, and ONLY 1 keyhole arrow is clear at the start! Trace carefully!",
    cols: 6,
    rows: 6,
    difficultyStars: 3,
    difficultyLabel: "Challenging",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXX.", "XXXXXX", "XXXXXX", "XX..XX", "XXXXXX", ".XXXX."]
  },
  {
    levelNumber: 5,
    title: "BOSS: The Cluttered Messy House",
    chapterName: "Ch. 1 • Cozy Book & Yarn Cafe",
    shapeName: "6×6 Messy Living Room",
    accentBadge: "★★★☆☆ Ch. 1 Boss",
    host: "SANDRA",
    hostTip: "CHAPTER 1 BOSS GATE! Beat the Messy House to unlock Chapter 2! Beware: blocked mistakes heal the Boss +20 HP!",
    cols: 6,
    rows: 6,
    difficultyStars: 3,
    difficultyLabel: "Boss Gate",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    bossType: "MESSY_HOUSE",
    mask: [".XXXX.", "XXXXXX", "XXXXXX", "XXXXXX", "XXXXXX", ".XXXX."]
  },
  {
    levelNumber: 6,
    title: "Ruby Tomato Labyrinth (7×7)",
    chapterName: "Ch. 2 • Naty's Artisan Bakery & Grill",
    shapeName: "7×7 Tomato Magic Ring",
    accentBadge: "★★★☆☆ Challenging",
    host: "NATY",
    hostTip: "Welcome to Chapter 2 (7×7 Grids)! 4- and 5-cell serpentine arrows wrap around the center ring—find the single keyhole!",
    cols: 7,
    rows: 7,
    difficultyStars: 3,
    difficultyLabel: "Challenging",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXXX.", "XXXXXXX", "XX...XX", "XX.X.XX", "XX...XX", "XXXXXXX", ".XXXXX."]
  },
  {
    levelNumber: 7,
    title: "Berry Buttercream Spiral",
    chapterName: "Ch. 2 • Naty's Artisan Bakery & Grill",
    shapeName: "7×7 Piped Frosting Dome",
    accentBadge: "★★★☆☆ Challenging",
    host: "ALLY",
    hostTip: "Ally's buttercream swirls point inward across the board! Don't get fooled by edge arrows whose rays hit the far wall.",
    cols: 7,
    rows: 7,
    difficultyStars: 3,
    difficultyLabel: "Challenging",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["..XXX..", ".XXXXX.", "XXXXXXX", "XXXXXXX", "XXXXXXX", ".XXXXX.", ".XXXXX."]
  },
  {
    levelNumber: 8,
    title: "Tortoiseshell Spectacle Lock",
    chapterName: "Ch. 2 • Naty's Artisan Bakery & Grill",
    shapeName: "7×7 Bookworm Frames",
    accentBadge: "★★★★☆ Hard",
    host: "SANDRA",
    hostTip: "Two interlocking lens rings! Beat this level to unlock Ally's 9×9 Daily Trophy Challenge!",
    cols: 7,
    rows: 7,
    difficultyStars: 4,
    difficultyLabel: "Hard",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["XXXXXXX", "XX.X.XX", "XXXXXXX", ".XXXXX.", "XXXXXXX", "XX.X.XX", ".XXXXX."]
  },
  {
    levelNumber: 9,
    title: "Master Baker's Wire Whisk",
    chapterName: "Ch. 2 • Naty's Artisan Bakery & Grill",
    shapeName: "7×7 Cross-Woven Whisk",
    accentBadge: "★★★★☆ Hard",
    host: "NATY",
    hostTip: "43 dense cells on a 7×7 grid! Save your Power-Ups for the Broken AC Boss waiting on Level 10!",
    cols: 7,
    rows: 7,
    difficultyStars: 4,
    difficultyLabel: "Hard",
    startingHearts: 3,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXXX.", "XXXXXXX", "XXX.XXX", "XXXXXXX", ".XXXXX.", "..XXX..", "..XXX.."]
  },
  {
    levelNumber: 10,
    title: "BOSS: Broken Air Conditioner (89°F)",
    chapterName: "Ch. 2 • Naty's Artisan Bakery & Grill",
    shapeName: "7×7 Overheated Condenser",
    accentBadge: "★★★★☆ Ch. 2 Boss",
    host: "NATY",
    hostTip: "CHAPTER 2 BOSS GATE (2 Hearts Only)! Cool the 89°F Broken AC without making blocked mistakes to unlock Chapter 3!",
    cols: 7,
    rows: 7,
    difficultyStars: 4,
    difficultyLabel: "Hard Boss",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    bossType: "BROKEN_AC",
    mask: [".XXXXX.", "XXXXXXX", "XXXXXXX", "XXX.XXX", "XXXXXXX", "XXXXXXX", ".XXXXX."]
  },
  {
    levelNumber: 11,
    title: "Crinkle-Cut Fry Fortress (8×8)",
    chapterName: "Ch. 3 • Ally's Gourmet Book Club",
    shapeName: "8×8 Golden Fry Carton",
    accentBadge: "★★★★☆ Hard • 2 Hearts",
    host: "ALLY",
    hostTip: "Welcome to Chapter 3! All 8×8 puzzles give you ONLY 2 Crochet Hearts—every single move requires pure strategic deduction!",
    cols: 8,
    rows: 8,
    difficultyStars: 4,
    difficultyLabel: "Hard",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["XX.XX.XX", "XXXXXXXX", "XXXXXXXX", ".XXXXXX.", "XXXXXXXX", "XXXXXXXX", ".XXXXXX.", "..XXXX.."]
  },
  {
    levelNumber: 12,
    title: "Brioche Dough Labyrinth (8×8)",
    chapterName: "Ch. 3 • Ally's Gourmet Book Club",
    shapeName: "8×8 Baker's Pastry Wheel",
    accentBadge: "★★★★☆ Hard • 2 Hearts",
    host: "NATY",
    hostTip: "5-cell winding yarn arrows interlock across the center hub. Unwind the outer spokes in exact reverse sequence!",
    cols: 8,
    rows: 8,
    difficultyStars: 4,
    difficultyLabel: "Hard",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["..XXXX..", ".XXXXXX.", "XXXXXXXX", "XXX..XXX", "XXX..XXX", "XXXXXXXX", ".XXXXXX.", "..XXXX.."]
  },
  {
    levelNumber: 13,
    title: "Double-Decker Smash Tower (8×8)",
    chapterName: "Ch. 3 • Ally's Gourmet Book Club",
    shapeName: "8×8 Double Cheeseburger",
    accentBadge: "★★★★☆ Hard • 2 Hearts",
    host: "SANDRA",
    hostTip: "58 cells packed into an 8×8 Double Cheeseburger! Beat this level to unlock 8×8 Endless Madness!",
    cols: 8,
    rows: 8,
    difficultyStars: 4,
    difficultyLabel: "Hard",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXXXX.", "XXXXXXXX", "XXXXXXXX", ".XXXXXX.", "XXXXXXXX", "XXXXXXXX", "XXXXXXXX", ".XXXXXX."]
  },
  {
    levelNumber: 14,
    title: "Midnight Book Club Vault (8×8)",
    chapterName: "Ch. 3 • Ally's Gourmet Book Club",
    shapeName: "8×8 Hardcover Archive",
    accentBadge: "★★★★★ Expert • 2 Hearts",
    host: "SANDRA",
    hostTip: "60 interlocking cells! Trace every horizontal and vertical corridor twice before committing your click.",
    cols: 8,
    rows: 8,
    difficultyStars: 5,
    difficultyLabel: "Expert",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXXXX.", "XXXXXXXX", "XXX..XXX", "XXXXXXXX", "XXXXXXXX", "XXX..XXX", "XXXXXXXX", ".XXXXXX."]
  },
  {
    levelNumber: 15,
    title: "BOSS: Messy Closet & Yarn Tornado",
    chapterName: "Ch. 3 • Ally's Gourmet Book Club",
    shapeName: "8×8 Overflowing Closet",
    accentBadge: "★★★★★ Ch. 3 Boss",
    host: "ALLY",
    hostTip: "CHAPTER 3 BOSS GATE (350 HP, 2 Hearts)! Conquer the 8×8 Closet Tornado to earn entry into Chapter 4's 9×9 & 10×10 Grids!",
    cols: 8,
    rows: 8,
    difficultyStars: 5,
    difficultyLabel: "Expert Boss",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    bossType: "LAUNDRY_YARN_TORNADO",
    mask: ["XXXXXXXX", "XX.XX.XX", "XXXXXXXX", "X.XXXX.X", "XXXXXXXX", "XX.XX.XX", "XXXXXXXX", ".XXXXXX."]
  },
  {
    levelNumber: 16,
    title: "Grandmaster Bookmark Star (9×9)",
    chapterName: "Ch. 4 • 10×10 Grandmaster & Bosses",
    shapeName: "9×9 Reading Star Matrix",
    accentBadge: "★★★★★ Grandmaster 9×9",
    host: "SANDRA",
    hostTip: "Welcome to Chapter 4! 9×9 grid, 2 Hearts, and ultra-narrow interlocking arrows—only true tacticians make it here!",
    cols: 9,
    rows: 9,
    difficultyStars: 5,
    difficultyLabel: "Grandmaster",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["..XXXXX..", ".XXXXXXX.", "XXXXXXXXX", "XXXX.XXXX", "XXX...XXX", "XXXX.XXXX", "XXXXXXXXX", ".XXXXXXX.", "..XXXXX.."]
  },
  {
    levelNumber: 17,
    title: "Brioche Crown Imperial (9×9)",
    chapterName: "Ch. 4 • 10×10 Grandmaster & Bosses",
    shapeName: "9×9 Royal Bakery Crown",
    accentBadge: "★★★★★ Grandmaster 9×9",
    host: "NATY",
    hostTip: "75 tightly woven cells on a 9×9 grid! Chain 8x+ combos without a mistake to earn bonus Power-Up charges!",
    cols: 9,
    rows: 9,
    difficultyStars: 5,
    difficultyLabel: "Grandmaster",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["X..XXX..X", "XX.XXX.XX", "XXXXXXXXX", "XXXXXXXXX", ".XXXXXXX.", "XXXXXXXXX", "XXXXXXXXX", "XXXXXXXXX", ".XXXXXXX."]
  },
  {
    levelNumber: 18,
    title: "Sandra, Naty & Ally's 9×9 Banquet",
    chapterName: "Ch. 4 • 10×10 Grandmaster & Bosses",
    shapeName: "9×9 Triple-Crown Platter",
    accentBadge: "★★★★★ Grandmaster 9×9",
    host: "ALLY",
    hostTip: "77 cells of pure strategic deduction! Beat this level to unlock the legendary 10×10 Grandmaster grids!",
    cols: 9,
    rows: 9,
    difficultyStars: 5,
    difficultyLabel: "Grandmaster",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: [".XXXXXXX.", "XXXXXXXXX", "XXXX.XXXX", "XXXXXXXXX", "XXX.X.XXX", "XXXXXXXXX", "XXXX.XXXX", "XXXXXXXXX", ".XXXXXXX."]
  },
  {
    levelNumber: 19,
    title: "Colossal 10×10 Burger & Bakery Citadel",
    chapterName: "Ch. 4 • 10×10 Grandmaster & Bosses",
    shapeName: "10×10 Sky-High Burger Stack",
    accentBadge: "★★★★★ 10×10 Citadel",
    host: "NATY",
    hostTip: "92 cells on a full 10×10 grid with only 1 starting keyhole! Every move unlocks the next link in the chain!",
    cols: 10,
    rows: 10,
    difficultyStars: 5,
    difficultyLabel: "Grandmaster",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    mask: ["..XXXXXX..", ".XXXXXXXX.", "XXXXXXXXXX", "XXXXXXXXXX", "XXXX..XXXX", "XXXX..XXXX", "XXXXXXXXXX", "XXXXXXXXXX", ".XXXXXXXX.", "..XXXXXX.."]
  },
  {
    levelNumber: 20,
    title: "FINAL BOSS: Messy House + Broken AC (10×10)",
    chapterName: "Ch. 4 • 10×10 Grandmaster & Bosses",
    shapeName: "10×10 Ultimate Burger & Chore Boss",
    accentBadge: "★★★★★ 10×10 Final Boss",
    host: "SANDRA",
    hostTip: "THE 10×10 ULTIMATE FINALE (460 HP, 96 Cells, 2 Hearts)! Clean the Messy House AND repair the 92°F Broken AC to claim the Sesame Crown!",
    cols: 10,
    rows: 10,
    difficultyStars: 5,
    difficultyLabel: "Final Boss",
    startingHearts: 2,
    allowRayGuide: false,
    maxInitialOpenings: 1,
    bossType: "ULTIMATE_CHORE_DUO",
    mask: [".XXXXXXXX.", "XXXXXXXXXX", "XXXXXXXXXX", "XX.XXXX.XX", "XXXXXXXXXX", "XXXXXXXXXX", "XX.XXXX.XX", "XXXXXXXXXX", "XXXXXXXXXX", ".XXXXXXXX."]
  }
];

export const TOTAL_STORY_LEVELS = STORY_LEVEL_METAS.length;

function createSeededRng(seed: number) {
  let state = Math.abs(Math.floor(seed)) || 1234567;
  return () => {
    state = (state * 1664525 + 1013904223) % 4294967296;
    return state / 4294967296;
  };
}

function parseMask(mask: string[], cols: number, rows: number): GridCell[] {
  const cells: GridCell[] = [];
  for (let r = 0; r < rows; r++) {
    const line = mask[r] || "";
    for (let c = 0; c < cols; c++) {
      if (line[c] === "X") {
        cells.push({ col: c, row: r });
      }
    }
  }
  return cells;
}

export function generateGuaranteedSolvableArrows(
  activeCells: GridCell[],
  cols: number,
  rows: number,
  seed: number,
  maxArrowLen: number,
  maxInitialOpenings = 1
): PuzzleArrow[] {
  const rng = createSeededRng(seed);
  const remainingMap = new Map<string, GridCell>();
  activeCells.forEach(c => remainingMap.set(cellKey(c), c));

  const peeledSet = new Set<string>();
  let lastPeeledSet = new Set<string>();
  const peeledArrows: PuzzleArrow[] = [];
  const dirKeys: DirectionKey[] = ["UP", "DOWN", "LEFT", "RIGHT"];
  let nextId = 1;

  while (remainingMap.size > 0) {
    const candidates: Array<{ cell: GridCell; dir: DirectionKey }> = [];
    remainingMap.forEach(cell => {
      for (const dir of dirKeys) {
        if (isRayClearOfRemaining(cell, dir, remainingMap, cols, rows)) {
          candidates.push({ cell, dir });
        }
      }
    });

    const isOpeningArrow = peeledArrows.length < maxInitialOpenings;
    let bestScore = -9999999;
    let topCandidates: Array<{ cell: GridCell; dir: DirectionKey }> = [];

    for (const cand of candidates) {
      const stats = measureRayDependencies(cand.cell, cand.dir, peeledSet, lastPeeledSet, cols, rows);
      const backNeighbor = stepCell(cand.cell, oppositeDirection(cand.dir));
      const hasTailNeighbor = remainingMap.has(cellKey(backNeighbor));

      let score = 0;
      if (isOpeningArrow) {
        score = (hasTailNeighbor ? 400 : 0) + stats.stepsToEdge * 35;
      } else {
        if (stats.lastPeeledHits > 0) {
          score += 2500 + stats.lastPeeledHits * 350;
        } else if (stats.totalPeeledHits > 0) {
          score += 1100 + stats.totalPeeledHits * 220;
        } else {
          score -= 5000;
        }
        if (hasTailNeighbor) score += 260;
        score += stats.stepsToEdge * 30;
      }

      if (score > bestScore) {
        bestScore = score;
        topCandidates = [cand];
      } else if (score >= bestScore - 120) {
        topCandidates.push(cand);
      }
    }

    const chosen = topCandidates[Math.floor(rng() * topCandidates.length)];
    const headCell = chosen.cell;
    const headDir = chosen.dir;

    const roll = rng();
    const desiredLen = maxArrowLen <= 1 ? 1 : roll < 0.04 ? 2 : roll < 0.38 ? Math.min(3, maxArrowLen) : roll < 0.78 ? Math.min(4, maxArrowLen) : maxArrowLen;

    const chainFromHeadToTail: GridCell[] = [headCell];
    let current = headCell;
    let lastStepDir = oppositeDirection(headDir);
    let firstStepTaken = false;

    while (chainFromHeadToTail.length < desiredLen) {
      const candidateDirs: DirectionKey[] = !firstStepTaken
        ? [oppositeDirection(headDir)]
        : [
            ...dirKeys.filter(d => d !== lastStepDir && d !== oppositeDirection(lastStepDir)).sort(() => rng() - 0.5),
            lastStepDir
          ];

      let nextNeighbor: GridCell | null = null;
      let chosenStepDir: DirectionKey | null = null;

      for (const d of candidateDirs) {
        const neighbor = stepCell(current, d);
        const k = cellKey(neighbor);
        if (
          remainingMap.has(k) &&
          !chainFromHeadToTail.some(existing => existing.col === neighbor.col && existing.row === neighbor.row)
        ) {
          nextNeighbor = neighbor;
          chosenStepDir = d;
          break;
        }
      }

      if (!nextNeighbor || !chosenStepDir) break;
      chainFromHeadToTail.push(nextNeighbor);
      current = nextNeighbor;
      lastStepDir = chosenStepDir;
      firstStepTaken = true;
    }

    const orderedCells = [...chainFromHeadToTail].reverse();
    const nextLastSet = new Set<string>();
    for (const c of orderedCells) {
      const k = cellKey(c);
      remainingMap.delete(k);
      peeledSet.add(k);
      nextLastSet.add(k);
    }
    lastPeeledSet = nextLastSet;

    const color = YARN_PALETTE[nextId % YARN_PALETTE.length];
    peeledArrows.push({
      id: nextId++,
      cells: orderedCells,
      headDirection: headDir,
      yarnColor: color
    });
  }

  return peeledArrows;
}

function isRayClearOfRemaining(
  start: GridCell,
  dir: DirectionKey,
  remainingMap: Map<string, GridCell>,
  cols: number,
  rows: number
): boolean {
  let cursor = stepCell(start, dir);
  while (cursor.col >= 0 && cursor.col < cols && cursor.row >= 0 && cursor.row < rows) {
    if (remainingMap.has(cellKey(cursor))) return false;
    cursor = stepCell(cursor, dir);
  }
  return true;
}

function measureRayDependencies(
  start: GridCell,
  dir: DirectionKey,
  peeledSet: Set<string>,
  lastPeeledSet: Set<string>,
  cols: number,
  rows: number
) {
  let stepsToEdge = 0;
  let totalPeeledHits = 0;
  let lastPeeledHits = 0;
  let cursor = stepCell(start, dir);
  while (cursor.col >= 0 && cursor.col < cols && cursor.row >= 0 && cursor.row < rows) {
    stepsToEdge++;
    const k = cellKey(cursor);
    if (peeledSet.has(k)) totalPeeledHits++;
    if (lastPeeledSet.has(k)) lastPeeledHits++;
    cursor = stepCell(cursor, dir);
  }
  return { stepsToEdge, totalPeeledHits, lastPeeledHits };
}

export function findFirstBlockingCell(
  arrow: PuzzleArrow,
  allActiveArrows: PuzzleArrow[],
  cols: number,
  rows: number
): GridCell | null {
  const occupied = new Set<string>();
  for (const other of allActiveArrows) {
    if (other.id !== arrow.id) {
      for (const c of other.cells) occupied.add(cellKey(c));
    }
  }
  const head = arrow.cells[arrow.cells.length - 1];
  let cursor = stepCell(head, arrow.headDirection);
  while (cursor.col >= 0 && cursor.col < cols && cursor.row >= 0 && cursor.row < rows) {
    if (occupied.has(cellKey(cursor))) return cursor;
    cursor = stepCell(cursor, arrow.headDirection);
  }
  return null;
}

export function countCurrentlyClearArrows(arrows: PuzzleArrow[], cols: number, rows: number): number {
  return arrows.filter(a => findFirstBlockingCell(a, arrows, cols, rows) === null).length;
}

export function getStoryLevel(levelNumber: number): LevelConfig {
  const meta = STORY_LEVEL_METAS.find(m => m.levelNumber === levelNumber) || STORY_LEVEL_METAS[0];
  const activeCells = parseMask(meta.mask, meta.cols, meta.rows);
  const maxLen = meta.levelNumber === 1 ? 3 : meta.levelNumber <= 5 ? 4 : 5;
  const arrows = generateGuaranteedSolvableArrows(
    activeCells,
    meta.cols,
    meta.rows,
    meta.levelNumber * 7919 + 313,
    maxLen,
    meta.maxInitialOpenings
  );
  return {
    levelNumber: meta.levelNumber,
    title: meta.title,
    chapterName: meta.chapterName,
    shapeName: meta.shapeName,
    accentBadge: meta.accentBadge,
    host: meta.host,
    hostTip: meta.hostTip,
    gridCols: meta.cols,
    gridRows: meta.rows,
    arrows,
    difficultyStars: meta.difficultyStars,
    difficultyLabel: meta.difficultyLabel,
    startingHearts: meta.startingHearts,
    parMoves: arrows.length,
    allowRayGuide: meta.allowRayGuide,
    bossType: meta.bossType
  };
}

export function getAllStoryLevels(): LevelConfig[] {
  return STORY_LEVEL_METAS.map(m => getStoryLevel(m.levelNumber));
}

export function getBossBattleLevel(bossId: BossTypeId): LevelConfig {
  const boss = BOSS_TYPES[bossId];
  const spec =
    bossId === "MESSY_HOUSE"
      ? { cols: 7, rows: 7, mask: ["..XXX..", ".XXXXX.", "XXXXXXX", "XXXXXXX", "XX.X.XX", "XXXXXXX", ".XXXXX."] }
      : bossId === "BROKEN_AC"
      ? { cols: 8, rows: 8, mask: [".XXXXXX.", "XXXXXXXX", "XX.XX.XX", "XXXXXXXX", "XXXXXXXX", "XX.XX.XX", "XXXXXXXX", ".XXXXXX."] }
      : bossId === "LAUNDRY_YARN_TORNADO"
      ? { cols: 9, rows: 9, mask: ["..XXXXX..", ".XXXXXXX.", "XXXXXXXXX", "XX.XXX.XX", "XXXXXXXXX", "XX.XXX.XX", "XXXXXXXXX", ".XXXXXXX.", "..XXXXX.."] }
      : { cols: 10, rows: 10, mask: [".XXXXXXXX.", "XXXXXXXXXX", "XXXXXXXXXX", "XX.XXXX.XX", "XXXXXXXXXX", "XXXXXXXXXX", "XX.XXXX.XX", "XXXXXXXXXX", "XXXXXXXXXX", ".XXXXXXXX."] };

  const activeCells = parseMask(spec.mask, spec.cols, spec.rows);
  const arrows = generateGuaranteedSolvableArrows(activeCells, spec.cols, spec.rows, boss.unlockLevel * 4999 + 777, 5, 1);
  return {
    levelNumber: 300 + boss.unlockLevel,
    title: `BOSS: ${boss.bossName}`,
    chapterName: `Turn-Based Boss Arena • ${spec.cols}×${spec.rows} Grid`,
    shapeName: boss.subtitle,
    accentBadge: "★★★★★ Boss Arena",
    host: "SANDRA",
    hostTip: `Sandra: ${boss.attackDescription}`,
    gridCols: spec.cols,
    gridRows: spec.rows,
    arrows,
    difficultyStars: 5,
    difficultyLabel: "Boss Arena",
    startingHearts: 2,
    parMoves: arrows.length,
    allowRayGuide: false,
    bossType: bossId
  };
}

export function generateEndlessMadnessLevel(gridSize: number, round: number, seed: number): LevelConfig {
  const cols = Math.min(10, Math.max(5, gridSize));
  const rows = cols;
  const activeCells: GridCell[] = [];
  for (let r = 0; r < rows; r++) {
    for (let c = 0; c < cols; c++) {
      const isCorner = (r === 0 || r === rows - 1) && (c === 0 || c === cols - 1);
      if (!isCorner) activeCells.push({ col: c, row: r });
    }
  }
  const arrows = generateGuaranteedSolvableArrows(activeCells, cols, rows, seed + round * 1337, cols >= 7 ? 5 : 4, 1);
  const hosts: CrewMemberId[] = ["SANDRA", "NATY", "ALLY"];
  const host = hosts[round % hosts.length];
  return {
    levelNumber: 200 + round,
    title: `Madness Recipe #${round} (${cols}×${rows})`,
    chapterName: "Endless • Sandra's Strategic Madness",
    shapeName: `${cols}×${rows} Interlocking Weave`,
    accentBadge: `Endless ${cols}×${rows}`,
    host,
    hostTip: `Round #${round} (${cols}×${rows})! Only 1 keyhole opening starts unlocked—unravel the chain with precision!`,
    gridCols: cols,
    gridRows: rows,
    arrows,
    difficultyStars: cols >= 9 ? 5 : 4,
    difficultyLabel: cols >= 9 ? "Grandmaster" : "Hard",
    startingHearts: cols >= 8 ? 2 : 3,
    parMoves: arrows.length,
    allowRayGuide: false,
    isEndlessMode: true
  };
}
