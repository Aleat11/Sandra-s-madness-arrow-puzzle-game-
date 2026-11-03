package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ArrowAllyBerryPink
import com.example.ui.theme.ArrowBriocheBrown
import com.example.ui.theme.ArrowCobaltYarn
import com.example.ui.theme.ArrowKetchupRed
import com.example.ui.theme.ArrowLettuceGreen
import com.example.ui.theme.ArrowNatyTeal
import com.example.ui.theme.ArrowOnionPurple
import com.example.ui.theme.ArrowSesameAmber

enum class Direction(val dx: Int, val dy: Int, val symbol: String) {
    UP(0, -1, "↑"),
    DOWN(0, 1, "↓"),
    LEFT(-1, 0, "←"),
    RIGHT(1, 0, "→");

    fun opposite(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}

data class GridCell(val col: Int, val row: Int) {
    fun step(dir: Direction, steps: Int = 1): GridCell =
        GridCell(col + dir.dx * steps, row + dir.dy * steps)
}

enum class YarnArrowColor(
    val displayName: String,
    val primaryColor: Color,
    val highlightColor: Color,
    val deepShadowColor: Color
) {
    KETCHUP_RED(
        displayName = "Ketchup & Bookmark Red",
        primaryColor = ArrowKetchupRed,
        highlightColor = Color(0xFFFF8A80),
        deepShadowColor = Color(0xFF9B1C1C)
    ),
    SESAME_GOLD(
        displayName = "Golden Baked Brioche",
        primaryColor = ArrowSesameAmber,
        highlightColor = Color(0xFFFDE68A),
        deepShadowColor = Color(0xFFB45309)
    ),
    LETTUCE_GREEN(
        displayName = "Crispy Lettuce Stitch",
        primaryColor = ArrowLettuceGreen,
        highlightColor = Color(0xFF86EFAC),
        deepShadowColor = Color(0xFF166534)
    ),
    NATY_TEAL(
        displayName = "Naty's Hook & Cool AC Teal",
        primaryColor = ArrowNatyTeal,
        highlightColor = Color(0xFF7DD3FC),
        deepShadowColor = Color(0xFF075985)
    ),
    ALLY_PINK(
        displayName = "Ally's Cupcake Frosting Pink",
        primaryColor = ArrowAllyBerryPink,
        highlightColor = Color(0xFFF9A8D4),
        deepShadowColor = Color(0xFF9D174D)
    ),
    ONION_PLUM(
        displayName = "Storybook Velvet Plum",
        primaryColor = ArrowOnionPurple,
        highlightColor = Color(0xFFC4B5FD),
        deepShadowColor = Color(0xFF5B21B6)
    ),
    BRIOCHE_BROWN(
        displayName = "Sizzling Patty & Cocoa",
        primaryColor = ArrowBriocheBrown,
        highlightColor = Color(0xFFD6A374),
        deepShadowColor = Color(0xFF4E2610)
    ),
    COBALT_STITCH(
        displayName = "Reading Nook Denim",
        primaryColor = ArrowCobaltYarn,
        highlightColor = Color(0xFF93C5FD),
        deepShadowColor = Color(0xFF1E40AF)
    )
}

/**
 * Custom Arrow/Pin visual style blending minimalist Arrow Puzzle geometry with Crochet & Burger pins.
 */
enum class ArrowPinStyle(val title: String, val subtitle: String) {
    MINIMAL_NARROW(
        title = "Narrow Minimalist Arrow",
        subtitle = "Ultra-sleek narrow Arrow Puzzle shaft with crisp chevron head"
    ),
    CROCHET_HOOK_PIN(
        title = "Crochet Hook & Yarn Pin",
        subtitle = "Narrow shaft with stitched yarn ribs and a golden hook tail bead"
    ),
    BURGER_SKEWER_PIN(
        title = "Burger Bamboo Skewer Pin",
        subtitle = "Slender toothpick skewer arrow with a sesame bun pin head at the tail"
    )
}

/**
 * Custom Crochet & Hamburger board background weave textures.
 */
enum class BoardBackgroundStyle(
    val title: String,
    val lightBg: Color,
    val darkBg: Color,
    val borderLight: Color,
    val borderDark: Color
) {
    WAFFLE_LINEN(
        title = "Cozy Waffle-Stitch Linen",
        lightBg = Color(0xFFFFFAF2),
        darkBg = Color(0xFF261E1A),
        borderLight = Color(0xFFE5C9AD),
        borderDark = Color(0xFF5C4338)
    ),
    SESAME_KRAFT(
        title = "Sesame Burger Diner Wrap",
        lightBg = Color(0xFFFFF3E0),
        darkBg = Color(0xFF2C2016),
        borderLight = Color(0xFFD99B52),
        borderDark = Color(0xFF784B23)
    ),
    MINT_BAKERY_CLOTH(
        title = "Cool Mint Bakery Tablecloth",
        lightBg = Color(0xFFF0FDF4),
        darkBg = Color(0xFF162621),
        borderLight = Color(0xFF86EFAC),
        borderDark = Color(0xFF2D5A47)
    )
}

/**
 * Earned Power-Ups that players must ration strategically during tough puzzles and Boss Fights.
 */
enum class PowerUpType(
    val title: String,
    val shortLabel: String,
    val description: String,
    val accentColor: Color
) {
    SPATULA_SNIP(
        title = "Golden Spatula Snip",
        shortLabel = "Snip 1",
        description = "Flips and removes 1 arrow immediately—even if its path is blocked!",
        accentColor = Color(0xFFF59E0B)
    ),
    YARN_BOMB(
        title = "Burger Yarn Triple-Blast",
        shortLabel = "3x Blast",
        description = "Launches up to 3 currently unobstructed arrows off the grid at once!",
        accentColor = Color(0xFFE53935)
    ),
    XRAY_SPECS(
        title = "Sandra's X-Ray Specs",
        shortLabel = "X-Ray Peek",
        description = "Reveals up to 2 clear keyhole arrows for your current move only!",
        accentColor = Color(0xFF10B981)
    ),
    WARM_BRIOCHE(
        title = "Naty's Shielded Brioche",
        shortLabel = "+Heart/Shield",
        description = "Restores +1 Crochet Heart and gives you a 1-hit Bump Shield!",
        accentColor = Color(0xFFEC4899)
    )
}

/**
 * Turn-Based Boss Fight types where clearing arrows fires projectiles at the Boss,
 * and each Boss must be unlocked by progressing through Story Chapters!
 */
enum class BossType(
    val bossName: String,
    val subtitle: String,
    val unlockLevel: Int,
    val maxHp: Int,
    val playerMovesPerTurn: Int,
    val attackDescription: String,
    val victoryQuote: String,
    val badgeColor: Color
) {
    MESSY_HOUSE(
        bossName = "The Cluttered Messy House",
        subtitle = "Mount Laundry, Dusty Sofa & Sink Full of Dishes",
        unlockLevel = 5,
        maxHp = 220,
        playerMovesPerTurn = 3,
        attackDescription = "Throws dust-bunnies & unfolded laundry every 3 moves—and repairs HP if you hit a blocked arrow!",
        victoryQuote = "Sandra: Spotless! Every dish is washed, the laundry is folded, and our Crochet Cafe is sparkling!",
        badgeColor = Color(0xFF8B5CF6)
    ),
    BROKEN_AC(
        bossName = "Broken Air Conditioner (89°F!)",
        subtitle = "Rattling Vents, Dripping Pipe & 89°F Heatwave",
        unlockLevel = 10,
        maxHp = 280,
        playerMovesPerTurn = 3,
        attackDescription = "Blasts 89°F hot air every 3 moves—fire strategic arrow chains to cool the coils to 68°F!",
        victoryQuote = "Naty: Ahh, 68°F icy cool breeze! The Broken AC is fixed and our buttercream frosting won't melt!",
        badgeColor = Color(0xFF0EA5E9)
    ),
    LAUNDRY_YARN_TORNADO(
        bossName = "Messy Closet & Tangled Skein",
        subtitle = "Overflowing Hamper & Knotted Yarn Pile",
        unlockLevel = 15,
        maxHp = 350,
        playerMovesPerTurn = 3,
        attackDescription = "Spins a whirlwind of socks and yarn knots across the 8×8 grid on its turn!",
        victoryQuote = "Ally: Closet organized and color-coded! Not a single tangled thread left!",
        badgeColor = Color(0xFFEC4899)
    ),
    ULTIMATE_CHORE_DUO(
        bossName = "Mega Boss: Messy House + Broken AC",
        subtitle = "10×10 Chaos: 92°F AC Unit & Mountain of Chores",
        unlockLevel = 20,
        maxHp = 460,
        playerMovesPerTurn = 3,
        attackDescription = "Combines 92°F heatwave blasts with flying clutter on a massive 10×10 Grandmaster grid!",
        victoryQuote = "Sandra, Naty & Ally: ULTIMATE VICTORY! The house is pristine, the AC is purring at 68°F, and burgers are served!",
        badgeColor = Color(0xFFE53935)
    )
}

/**
 * Represents a single or multi-segment arrow on the grid, styled after Easybrain's Arrow Puzzle.
 * [cells] are ordered from tail (first) to arrowhead (last).
 */
data class PuzzleArrow(
    val id: Int,
    val cells: List<GridCell>,
    val headDirection: Direction,
    val yarnColor: YarnArrowColor
) {
    val headCell: GridCell
        get() = cells.last()

    val tailCell: GridCell
        get() = cells.first()
}

enum class CrewMember(
    val displayName: String,
    val roleTitle: String,
    val badgeColor: Color,
    val hairColor: Color,
    val shirtColor: Color,
    val bio: String,
    val specialty: String,
    val hobbyTag: String
) {
    SANDRA(
        displayName = "Sandra",
        roleTitle = "Crochet Chef, Bookworm & Boss Tamer",
        badgeColor = Color(0xFFD9532F),
        hairColor = Color(0xFF2D1B14),
        shirtColor = Color(0xFF2D2E32),
        bio = "The heart and star of Sandra's Madness! Sandra loves cozy reading, baking, crocheting gourmet burgers, and blasting arrows at messy houses & broken air conditioners.",
        specialty = "Unlocks X-Ray Specs, stitches emergency hearts & leads Turn-Based Boss Battles.",
        hobbyTag = "Crochet • Burgers • Reading • Bosses"
    ),
    NATY(
        displayName = "Naty",
        roleTitle = "Master Baker, AC Fixer & Hook Whisperer",
        badgeColor = Color(0xFF0EA5E9),
        hairColor = Color(0xFF2B1B17),
        shirtColor = Color(0xFF0284C7),
        bio = "With a baker's whisk in one hand and a crochet hook in the other, Naty bakes warm sesame buns and keeps the Broken AC from melting the frosting.",
        specialty = "Provides 1 Tactical Hint per level and bakes Shielded Brioche Buns.",
        hobbyTag = "Baking Buns • Crochet • Cool AC"
    ),
    ALLY(
        displayName = "Ally",
        roleTitle = "Pastry Toppings, Tidy Queen & Book Club Host",
        badgeColor = Color(0xFFEC4899),
        hairColor = Color(0xFF6D3B1A),
        shirtColor = Color(0xFFDB2777),
        bio = "Queen of frosted cupcakes, secret burger sauces, and tidy rooms! Ally helps Sandra sweep away the Messy House boss with combo arrow volleys.",
        specialty = "Crafts Burger Yarn Bombs and curates the Custom Game Assets & Stacker.",
        hobbyTag = "Pastries • Bookmarks • Toppings"
    )
}

enum class BurgerLayerType {
    BOTTOM_BUN,
    PATTY,
    CHEESE,
    LETTUCE,
    TOMATO,
    PICKLES,
    SECRET_SAUCE,
    TOP_BUN,
    BAKED_CROISSANT,
    FROSTED_CUPCAKE,
    RECIPE_STORYBOOK,
    BAKER_WHISK_BOOKMARK
}

enum class AccentCategory(val label: String) {
    BURGER("Crochet Burger"),
    BAKING("Sweet Baking"),
    READING("Cozy Reading")
}

data class BurgerYarnLayer(
    val id: String,
    val name: String,
    val stitchCount: Int,
    val unlockedAtLevel: Int,
    val craftedBy: CrewMember,
    val category: AccentCategory,
    val description: String,
    val layerType: BurgerLayerType,
    val primaryColor: Color,
    val accentColor: Color
)

data class LevelConfig(
    val levelNumber: Int,
    val title: String,
    val chapterName: String,
    val shapeName: String,
    val accentBadge: String,
    val host: CrewMember,
    val hostTip: String,
    val gridCols: Int,
    val gridRows: Int,
    val arrows: List<PuzzleArrow>,
    val stitchPattern: Map<GridCell, YarnArrowColor>,
    val difficultyStars: Int = 2,
    val difficultyLabel: String = "Tactical",
    val startingHearts: Int = 3,
    val parMoves: Int = arrows.size,
    val allowRayGuide: Boolean = false,
    val bossType: BossType? = null,
    val isDailyChallenge: Boolean = false,
    val isEndlessMode: Boolean = false
)
