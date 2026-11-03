export type DirectionKey = "UP" | "DOWN" | "LEFT" | "RIGHT";

export interface Direction {
  key: DirectionKey;
  dx: number;
  dy: number;
  symbol: string;
}

export const DIRECTIONS: Record<DirectionKey, Direction> = {
  UP: { key: "UP", dx: 0, dy: -1, symbol: "↑" },
  DOWN: { key: "DOWN", dx: 0, dy: 1, symbol: "↓" },
  LEFT: { key: "LEFT", dx: -1, dy: 0, symbol: "←" },
  RIGHT: { key: "RIGHT", dx: 1, dy: 0, symbol: "→" }
};

export function oppositeDirection(dir: DirectionKey): DirectionKey {
  switch (dir) {
    case "UP": return "DOWN";
    case "DOWN": return "UP";
    case "LEFT": return "RIGHT";
    case "RIGHT": return "LEFT";
  }
}

export interface GridCell {
  col: number;
  row: number;
}

export function stepCell(cell: GridCell, dir: DirectionKey, steps = 1): GridCell {
  const d = DIRECTIONS[dir];
  return { col: cell.col + d.dx * steps, row: cell.row + d.dy * steps };
}

export function cellKey(cell: GridCell): string {
  return `${cell.col},${cell.row}`;
}

export interface YarnArrowColor {
  id: string;
  displayName: string;
  primaryColor: string;
  highlightColor: string;
  deepShadowColor: string;
}

export const YARN_PALETTE: YarnArrowColor[] = [
  {
    id: "KETCHUP_RED",
    displayName: "Ketchup & Bookmark Red",
    primaryColor: "#E53935",
    highlightColor: "#FF8A80",
    deepShadowColor: "#9B1C1C"
  },
  {
    id: "SESAME_GOLD",
    displayName: "Golden Baked Brioche",
    primaryColor: "#F59E0B",
    highlightColor: "#FDE68A",
    deepShadowColor: "#B45309"
  },
  {
    id: "LETTUCE_GREEN",
    displayName: "Crispy Lettuce Stitch",
    primaryColor: "#22C55E",
    highlightColor: "#86EFAC",
    deepShadowColor: "#166534"
  },
  {
    id: "NATY_TEAL",
    displayName: "Naty's Hook & Cool AC Teal",
    primaryColor: "#0EA5E9",
    highlightColor: "#7DD3FC",
    deepShadowColor: "#075985"
  },
  {
    id: "ALLY_PINK",
    displayName: "Ally's Cupcake Frosting Pink",
    primaryColor: "#EC4899",
    highlightColor: "#F9A8D4",
    deepShadowColor: "#9D174D"
  },
  {
    id: "ONION_PLUM",
    displayName: "Storybook Velvet Plum",
    primaryColor: "#8B5CF6",
    highlightColor: "#C4B5FD",
    deepShadowColor: "#5B21B6"
  },
  {
    id: "BRIOCHE_BROWN",
    displayName: "Sizzling Patty & Cocoa",
    primaryColor: "#78350F",
    highlightColor: "#D6A374",
    deepShadowColor: "#4E2610"
  },
  {
    id: "COBALT_STITCH",
    displayName: "Reading Nook Denim",
    primaryColor: "#2563EB",
    highlightColor: "#93C5FD",
    deepShadowColor: "#1E40AF"
  }
];

export type ArrowPinStyleId = "MINIMAL_NARROW" | "CROCHET_HOOK_PIN" | "BURGER_SKEWER_PIN";

export interface ArrowPinStyleInfo {
  id: ArrowPinStyleId;
  title: string;
  subtitle: string;
}

export const ARROW_PIN_STYLES: ArrowPinStyleInfo[] = [
  {
    id: "MINIMAL_NARROW",
    title: "Narrow Minimalist Arrow",
    subtitle: "Ultra-sleek narrow Arrow Puzzle shaft with crisp chevron head"
  },
  {
    id: "CROCHET_HOOK_PIN",
    title: "Crochet Hook & Yarn Pin",
    subtitle: "Narrow shaft with stitched yarn ribs and a golden hook tail bead"
  },
  {
    id: "BURGER_SKEWER_PIN",
    title: "Burger Bamboo Skewer Pin",
    subtitle: "Slender toothpick skewer arrow with a sesame bun pin head at the tail"
  }
];

export type BoardBackgroundStyleId = "WAFFLE_LINEN" | "SESAME_KRAFT" | "MINT_BAKERY_CLOTH";

export interface BoardBackgroundStyleInfo {
  id: BoardBackgroundStyleId;
  title: string;
  lightBg: string;
  darkBg: string;
  borderLight: string;
  borderDark: string;
}

export const BOARD_BG_STYLES: BoardBackgroundStyleInfo[] = [
  {
    id: "WAFFLE_LINEN",
    title: "Cozy Waffle-Stitch Linen",
    lightBg: "#FFFAF2",
    darkBg: "#261E1A",
    borderLight: "#E5C9AD",
    borderDark: "#5C4338"
  },
  {
    id: "SESAME_KRAFT",
    title: "Sesame Burger Diner Wrap",
    lightBg: "#FFF3E0",
    darkBg: "#2C2016",
    borderLight: "#D99B52",
    borderDark: "#784B23"
  },
  {
    id: "MINT_BAKERY_CLOTH",
    title: "Cool Mint Bakery Tablecloth",
    lightBg: "#F0FDF4",
    darkBg: "#162621",
    borderLight: "#86EFAC",
    borderDark: "#2D5A47"
  }
];

export type PowerUpTypeId = "SPATULA_SNIP" | "YARN_BOMB" | "XRAY_SPECS" | "WARM_BRIOCHE";

export interface PowerUpInfo {
  id: PowerUpTypeId;
  title: string;
  shortLabel: string;
  description: string;
  accentColor: string;
}

export const POWER_UPS: PowerUpInfo[] = [
  {
    id: "SPATULA_SNIP",
    title: "Golden Spatula Snip",
    shortLabel: "Snip 1",
    description: "Flips and removes 1 arrow immediately—even if its path is blocked!",
    accentColor: "#F59E0B"
  },
  {
    id: "YARN_BOMB",
    title: "Burger Yarn Triple-Blast",
    shortLabel: "3x Blast",
    description: "Launches up to 3 currently unobstructed arrows off the grid at once!",
    accentColor: "#E53935"
  },
  {
    id: "XRAY_SPECS",
    title: "Sandra's X-Ray Specs",
    shortLabel: "X-Ray Peek",
    description: "Reveals up to 2 clear keyhole arrows for your current move only!",
    accentColor: "#10B981"
  },
  {
    id: "WARM_BRIOCHE",
    title: "Naty's Shielded Brioche",
    shortLabel: "+Heart/Shield",
    description: "Restores +1 Crochet Heart and gives you a 1-hit Bump Shield!",
    accentColor: "#EC4899"
  }
];

export type BossTypeId = "MESSY_HOUSE" | "BROKEN_AC" | "LAUNDRY_YARN_TORNADO" | "ULTIMATE_CHORE_DUO";

export interface BossInfo {
  id: BossTypeId;
  bossName: string;
  subtitle: string;
  unlockLevel: number;
  maxHp: number;
  playerMovesPerTurn: number;
  attackDescription: string;
  victoryQuote: string;
  badgeColor: string;
}

export const BOSS_TYPES: Record<BossTypeId, BossInfo> = {
  MESSY_HOUSE: {
    id: "MESSY_HOUSE",
    bossName: "The Cluttered Messy House",
    subtitle: "Mount Laundry, Dusty Sofa & Sink Full of Dishes",
    unlockLevel: 5,
    maxHp: 220,
    playerMovesPerTurn: 3,
    attackDescription: "Throws dust-bunnies & unfolded laundry every 3 moves—and repairs +20 HP if you hit a blocked arrow!",
    victoryQuote: "Sandra: Spotless! Every dish is washed, the laundry is folded, and our Crochet Cafe is sparkling!",
    badgeColor: "#8B5CF6"
  },
  BROKEN_AC: {
    id: "BROKEN_AC",
    bossName: "Broken Air Conditioner (89°F!)",
    subtitle: "Rattling Vents, Dripping Pipe & 89°F Heatwave",
    unlockLevel: 10,
    maxHp: 280,
    playerMovesPerTurn: 3,
    attackDescription: "Blasts 89°F hot air every 3 moves—fire strategic arrow chains to cool the coils to 68°F!",
    victoryQuote: "Naty: Ahh, 68°F icy cool breeze! The Broken AC is fixed and our buttercream frosting won't melt!",
    badgeColor: "#0EA5E9"
  },
  LAUNDRY_YARN_TORNADO: {
    id: "LAUNDRY_YARN_TORNADO",
    bossName: "Messy Closet & Tangled Skein",
    subtitle: "Overflowing Hamper & Knotted Yarn Pile",
    unlockLevel: 15,
    maxHp: 350,
    playerMovesPerTurn: 3,
    attackDescription: "Spins a whirlwind of socks and yarn knots across the 8×8 grid on its turn!",
    victoryQuote: "Ally: Closet organized and color-coded! Not a single tangled thread left!",
    badgeColor: "#EC4899"
  },
  ULTIMATE_CHORE_DUO: {
    id: "ULTIMATE_CHORE_DUO",
    bossName: "Mega Boss: Messy House + Broken AC",
    subtitle: "10×10 Chaos: 92°F AC Unit & Mountain of Chores",
    unlockLevel: 20,
    maxHp: 460,
    playerMovesPerTurn: 3,
    attackDescription: "Combines 92°F heatwave blasts with flying clutter on a massive 10×10 Grandmaster grid!",
    victoryQuote: "Sandra, Naty & Ally: ULTIMATE VICTORY! The house is pristine, the AC is purring at 68°F, and burgers are served!",
    badgeColor: "#E53935"
  }
};

export interface PuzzleArrow {
  id: number;
  cells: GridCell[];
  headDirection: DirectionKey;
  yarnColor: YarnArrowColor;
}

export type CrewMemberId = "SANDRA" | "NATY" | "ALLY";

export interface CrewMemberInfo {
  id: CrewMemberId;
  displayName: string;
  roleTitle: string;
  badgeColor: string;
  bio: string;
  specialty: string;
  hobbyTag: string;
}

export const CREW_MEMBERS: Record<CrewMemberId, CrewMemberInfo> = {
  SANDRA: {
    id: "SANDRA",
    displayName: "Sandra",
    roleTitle: "Crochet Chef, Bookworm & Boss Tamer",
    badgeColor: "#D9532F",
    bio: "The heart and star of Sandra's Madness! Sandra loves cozy reading, baking, crocheting gourmet burgers, and blasting arrows at messy houses & broken air conditioners.",
    specialty: "Unlocks X-Ray Specs, stitches emergency hearts & leads Turn-Based Boss Battles.",
    hobbyTag: "Crochet • Burgers • Reading • Bosses"
  },
  NATY: {
    id: "NATY",
    displayName: "Naty",
    roleTitle: "Master Baker, AC Fixer & Hook Whisperer",
    badgeColor: "#0EA5E9",
    bio: "With a baker's whisk in one hand and a crochet hook in the other, Naty bakes warm sesame buns and keeps the Broken AC from melting the frosting.",
    specialty: "Provides 1 Tactical Hint per level and bakes Shielded Brioche Buns.",
    hobbyTag: "Baking Buns • Crochet • Cool AC"
  },
  ALLY: {
    id: "ALLY",
    displayName: "Ally",
    roleTitle: "Pastry Toppings, Tidy Queen & Book Club Host",
    badgeColor: "#EC4899",
    bio: "Queen of frosted cupcakes, secret burger sauces, and tidy rooms! Ally helps Sandra sweep away the Messy House boss with combo arrow volleys.",
    specialty: "Crafts Burger Yarn Bombs and curates the Custom Game Assets & Stacker.",
    hobbyTag: "Pastries • Bookmarks • Toppings"
  }
};

export interface BurgerYarnLayer {
  id: string;
  name: string;
  stitchCount: number;
  unlockedAtLevel: number;
  craftedBy: CrewMemberId;
  category: "Crochet Burger" | "Sweet Baking" | "Cozy Reading";
  description: string;
  primaryColor: string;
  accentColor: string;
}

export interface LevelConfig {
  levelNumber: number;
  title: string;
  chapterName: string;
  shapeName: string;
  accentBadge: string;
  host: CrewMemberId;
  hostTip: string;
  gridCols: number;
  gridRows: number;
  arrows: PuzzleArrow[];
  difficultyStars: number;
  difficultyLabel: string;
  startingHearts: number;
  parMoves: number;
  allowRayGuide: boolean;
  bossType?: BossTypeId;
  isDailyChallenge?: boolean;
  isEndlessMode?: boolean;
}

export interface LaunchExplosionAnim {
  id: number;
  cells: GridCell[];
  primaryColor: string;
  highlightColor: string;
  comicText: string;
  comboCount: number;
  headCell: GridCell;
  startTimeMs: number;
}

export interface ExitingArrowAnim {
  arrow: PuzzleArrow;
  startTimeMs: number;
}

export interface BlockedArrowAnim {
  arrowId: number;
  blockingCell: GridCell;
  timestampMs: number;
}
