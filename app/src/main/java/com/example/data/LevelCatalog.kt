package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.AccentCategory
import com.example.model.BossType
import com.example.model.BurgerLayerType
import com.example.model.BurgerYarnLayer
import com.example.model.CrewMember
import com.example.model.Direction
import com.example.model.GridCell
import com.example.model.LevelConfig
import com.example.model.PuzzleArrow
import com.example.model.YarnArrowColor
import kotlin.random.Random

object LevelCatalog {

    const val DAILY_CHALLENGE_UNLOCK_LEVEL = 8

    fun isStoryLevelUnlocked(levelNumber: Int, highestUnlockedLevel: Int): Boolean {
        return levelNumber <= highestUnlockedLevel.coerceAtLeast(1)
    }

    fun isBossUnlocked(
        bossType: BossType,
        highestUnlockedLevel: Int,
        completedLevels: Set<Int>
    ): Boolean {
        return bossType.unlockLevel <= highestUnlockedLevel || bossType.unlockLevel in completedLevels
    }

    fun requiredLevelForEndlessSize(gridSize: Int): Int = when (gridSize) {
        5 -> 3
        6 -> 6
        7 -> 10
        8 -> 13
        9 -> 16
        else -> 19
    }

    fun isEndlessSizeUnlocked(gridSize: Int, highestUnlockedLevel: Int): Boolean {
        return highestUnlockedLevel >= requiredLevelForEndlessSize(gridSize)
    }

    fun isDailyChallengeUnlocked(highestUnlockedLevel: Int): Boolean {
        return highestUnlockedLevel >= DAILY_CHALLENGE_UNLOCK_LEVEL
    }

    val burgerCollectibles: List<BurgerYarnLayer> = listOf(
        BurgerYarnLayer(
            id = "bottom_bun",
            name = "Oven-Baked Brioche Yarn Base",
            stitchCount = 128,
            unlockedAtLevel = 1,
            craftedBy = CrewMember.NATY,
            category = AccentCategory.BURGER,
            description = "Fresh from Naty's warm oven and double-crocheted in golden brioche yarn.",
            layerType = BurgerLayerType.BOTTOM_BUN,
            primaryColor = Color(0xFFE59838),
            accentColor = Color(0xFFFFF3D6)
        ),
        BurgerYarnLayer(
            id = "recipe_storybook",
            name = "Sandra's Hardcover Recipe & Yarn Tome",
            stitchCount = 180,
            unlockedAtLevel = 2,
            craftedBy = CrewMember.SANDRA,
            category = AccentCategory.READING,
            description = "A cozy stitched hardcover novel & secret burger recipe book with a tasseled red yarn bookmark.",
            layerType = BurgerLayerType.RECIPE_STORYBOOK,
            primaryColor = Color(0xFF7C3AED),
            accentColor = Color(0xFFFDE68A)
        ),
        BurgerYarnLayer(
            id = "smash_patty",
            name = "Sizzling Amigurumi Smash Patty",
            unlockedAtLevel = 3,
            stitchCount = 210,
            craftedBy = CrewMember.SANDRA,
            category = AccentCategory.BURGER,
            description = "Textured waffle-stitch espresso yarn patty with caramelized char-loop edges.",
            layerType = BurgerLayerType.PATTY,
            primaryColor = Color(0xFF6B3418),
            accentColor = Color(0xFF9C522B)
        ),
        BurgerYarnLayer(
            id = "baked_croissant",
            name = "Golden Flaky Yarn Croissant",
            unlockedAtLevel = 4,
            stitchCount = 190,
            craftedBy = CrewMember.NATY,
            category = AccentCategory.BAKING,
            description = "Buttery spiral-ribbed pastry baked to perfection for Sandra's morning reading nook.",
            layerType = BurgerLayerType.BAKED_CROISSANT,
            primaryColor = Color(0xFFF59E0B),
            accentColor = Color(0xFFFEF3C7)
        ),
        BurgerYarnLayer(
            id = "cheddar_melt",
            name = "Melted Cheddar Corners",
            unlockedAtLevel = 5,
            stitchCount = 164,
            craftedBy = CrewMember.NATY,
            category = AccentCategory.BURGER,
            description = "Bright goldenrod yarn stitched with drooping triangular cheese corners.",
            layerType = BurgerLayerType.CHEESE,
            primaryColor = Color(0xFFFBBF24),
            accentColor = Color(0xFFF59E0B)
        ),
        BurgerYarnLayer(
            id = "wavy_lettuce",
            name = "Ruffled Crochet Lettuce",
            unlockedAtLevel = 7,
            stitchCount = 195,
            craftedBy = CrewMember.ALLY,
            category = AccentCategory.BURGER,
            description = "Hyperbolic ruffle-stitch emerald yarn that gives every burger a cozy crunch.",
            layerType = BurgerLayerType.LETTUCE,
            primaryColor = Color(0xFF34D399),
            accentColor = Color(0xFF10B981)
        ),
        BurgerYarnLayer(
            id = "frosted_cupcake",
            name = "Ally's Berry Swirl Baked Cupcake",
            unlockedAtLevel = 9,
            stitchCount = 225,
            craftedBy = CrewMember.ALLY,
            category = AccentCategory.BAKING,
            description = "Piped strawberry buttercream yarn swirls with rainbow stitched sprinkles and a cherry.",
            layerType = BurgerLayerType.FROSTED_CUPCAKE,
            primaryColor = Color(0xFFEC4899),
            accentColor = Color(0xFFFCE7F3)
        ),
        BurgerYarnLayer(
            id = "ruby_tomato",
            name = "Ruby Tomato Wheel",
            unlockedAtLevel = 11,
            stitchCount = 150,
            craftedBy = CrewMember.NATY,
            category = AccentCategory.BURGER,
            description = "Crimson magic-ring tomato slice with radial seed-pocket crochet spokes.",
            layerType = BurgerLayerType.TOMATO,
            primaryColor = Color(0xFFEF4444),
            accentColor = Color(0xFFFCA5A5)
        ),
        BurgerYarnLayer(
            id = "crinkle_pickles",
            name = "Crinkle-Stitch Dill Pickles",
            unlockedAtLevel = 13,
            stitchCount = 112,
            craftedBy = CrewMember.ALLY,
            category = AccentCategory.BURGER,
            description = "Three ribbed forest-green pickle medallions with lime yarn border ridges.",
            layerType = BurgerLayerType.PICKLES,
            primaryColor = Color(0xFF22C55E),
            accentColor = Color(0xFFBBF7D0)
        ),
        BurgerYarnLayer(
            id = "whisk_bookmark",
            name = "Baker's Whisk & Reading Glasses Charm",
            unlockedAtLevel = 15,
            stitchCount = 240,
            craftedBy = CrewMember.SANDRA,
            category = AccentCategory.READING,
            description = "Silver-thread pastry whisk paired with Sandra's cozy tortoiseshell reading glasses bookmark.",
            layerType = BurgerLayerType.BAKER_WHISK_BOOKMARK,
            primaryColor = Color(0xFF0EA5E9),
            accentColor = Color(0xFFE0F2FE)
        ),
        BurgerYarnLayer(
            id = "secret_sauce",
            name = "Ally's Madness Glaze & Sauce",
            unlockedAtLevel = 18,
            stitchCount = 175,
            craftedBy = CrewMember.ALLY,
            category = AccentCategory.BURGER,
            description = "Tangy coral-pink chain-stitch burger sauce blended with sweet bakery glaze.",
            layerType = BurgerLayerType.SECRET_SAUCE,
            primaryColor = Color(0xFFF43F5E),
            accentColor = Color(0xFFFECDD3)
        ),
        BurgerYarnLayer(
            id = "sesame_crown",
            name = "Golden Baked Sesame Crown Bun",
            unlockedAtLevel = 20,
            stitchCount = 290,
            craftedBy = CrewMember.SANDRA,
            category = AccentCategory.BURGER,
            description = "Plush oven-browned dome top bun embroidered with French-knot sesame seeds.",
            layerType = BurgerLayerType.TOP_BUN,
            primaryColor = Color(0xFFD97706),
            accentColor = Color(0xFFFEF3C7)
        )
    )

    private data class LevelMeta(
        val levelNumber: Int,
        val title: String,
        val chapterName: String,
        val shapeName: String,
        val accentBadge: String,
        val host: CrewMember,
        val hostTip: String,
        val cols: Int,
        val rows: Int,
        val mask: List<String>,
        val difficultyStars: Int,
        val difficultyLabel: String,
        val startingHearts: Int = 3,
        val allowRayGuide: Boolean = false,
        val maxInitialOpenings: Int = 1,
        val bossType: BossType? = null
    )

    private val storyLevelMetas: List<LevelMeta> = listOf(
        // CHAPTER 1: Sandra's Cozy Book & Yarn Cafe (Levels 1-5) — Progressive Tactical Onboarding
        LevelMeta(
            levelNumber = 1,
            title = "Warm Brioche Keyhole",
            chapterName = "Ch. 1 • Cozy Book & Yarn Cafe",
            shapeName = "5×5 Baked Crochet Bun",
            accentBadge = "★☆☆☆☆ Apprentice",
            host = CrewMember.SANDRA,
            hostTip = "Welcome to Sandra's Madness! Only 2 keyhole arrows are unblocked right now—trace each arrow's lane before tapping to unlock Level 2!",
            cols = 5,
            rows = 5,
            difficultyStars = 1,
            difficultyLabel = "Apprentice",
            startingHearts = 3,
            allowRayGuide = true,
            maxInitialOpenings = 2,
            mask = listOf(
                ".XXX.",
                "XXXXX",
                "XXXXX",
                "XXXXX",
                ".XXX."
            )
        ),
        LevelMeta(
            levelNumber = 2,
            title = "Hardcover Spine Lock",
            chapterName = "Ch. 1 • Cozy Book & Yarn Cafe",
            shapeName = "5×5 Interlocking Book",
            accentBadge = "★★☆☆☆ Tactical",
            host = CrewMember.SANDRA,
            hostTip = "Arrows bend around corners to block adjacent lanes! Find the 2 unblocked arrows to peel open the book spine.",
            cols = 5,
            rows = 5,
            difficultyStars = 2,
            difficultyLabel = "Tactical",
            startingHearts = 3,
            allowRayGuide = true,
            maxInitialOpenings = 2,
            mask = listOf(
                "XXXXX",
                "XXXXX",
                "XX.XX",
                "XXXXX",
                ".XXX."
            )
        ),
        LevelMeta(
            levelNumber = 3,
            title = "Sizzling 6×6 Smash Weave",
            chapterName = "Ch. 1 • Cozy Book & Yarn Cafe",
            shapeName = "6×6 Char-Loop Patty",
            accentBadge = "★★☆☆☆ Tactical",
            host = CrewMember.NATY,
            hostTip = "Stepping up to a 6×6 grid! Last level with Apprentice Ray Guide—learn to spot how L-shaped tails block cross-traffic!",
            cols = 6,
            rows = 6,
            difficultyStars = 2,
            difficultyLabel = "Tactical",
            startingHearts = 3,
            allowRayGuide = true,
            maxInitialOpenings = 2,
            mask = listOf(
                ".XXXX.",
                "XXXXXX",
                "XX..XX",
                "XX..XX",
                "XXXXXX",
                ".XXXX."
            )
        ),
        LevelMeta(
            levelNumber = 4,
            title = "Naty's Braided Croissant Knot",
            chapterName = "Ch. 1 • Cozy Book & Yarn Cafe",
            shapeName = "6×6 Braided Pastry",
            accentBadge = "★★★☆☆ Challenging",
            host = CrewMember.NATY,
            hostTip = "PRO RULES UNLOCKED: Free Ray Guide is now off, and ONLY 1 keyhole arrow is clear at the start! Trace carefully before every tap!",
            cols = 6,
            rows = 6,
            difficultyStars = 3,
            difficultyLabel = "Challenging",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXX.",
                "XXXXXX",
                "XXXXXX",
                "XX..XX",
                "XXXXXX",
                ".XXXX."
            )
        ),
        LevelMeta(
            levelNumber = 5,
            title = "BOSS: The Cluttered Messy House",
            chapterName = "Ch. 1 • Cozy Book & Yarn Cafe",
            shapeName = "6×6 Messy Living Room",
            accentBadge = "★★★☆☆ Ch. 1 Boss",
            host = CrewMember.SANDRA,
            hostTip = "CHAPTER 1 BOSS GATE! Beat the Messy House to unlock Chapter 2! Beware: blocked mistakes heal the Boss +20 HP!",
            cols = 6,
            rows = 6,
            difficultyStars = 3,
            difficultyLabel = "Boss Gate",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            bossType = BossType.MESSY_HOUSE,
            mask = listOf(
                ".XXXX.",
                "XXXXXX",
                "XXXXXX",
                "XXXXXX",
                "XXXXXX",
                ".XXXX."
            )
        ),
        // CHAPTER 2: Naty's Artisan Bakery & Grill (Levels 6-10) — 7x7 Single-Keyhole Chains
        LevelMeta(
            levelNumber = 6,
            title = "Ruby Tomato Labyrinth (7×7)",
            chapterName = "Ch. 2 • Naty's Artisan Bakery & Grill",
            shapeName = "7×7 Tomato Magic Ring",
            accentBadge = "★★★☆☆ Challenging",
            host = CrewMember.NATY,
            hostTip = "Welcome to Chapter 2 (7×7 Grids)! 4- and 5-cell serpentine arrows wrap around the center ring—find the single keyhole!",
            cols = 7,
            rows = 7,
            difficultyStars = 3,
            difficultyLabel = "Challenging",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXXX.",
                "XXXXXXX",
                "XX...XX",
                "XX.X.XX",
                "XX...XX",
                "XXXXXXX",
                ".XXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 7,
            title = "Berry Buttercream Spiral",
            chapterName = "Ch. 2 • Naty's Artisan Bakery & Grill",
            shapeName = "7×7 Piped Frosting Dome",
            accentBadge = "★★★☆☆ Challenging",
            host = CrewMember.ALLY,
            hostTip = "Ally's buttercream swirls point inward across the board! Don't get fooled by edge arrows whose rays hit the far wall.",
            cols = 7,
            rows = 7,
            difficultyStars = 3,
            difficultyLabel = "Challenging",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "..XXX..",
                ".XXXXX.",
                "XXXXXXX",
                "XXXXXXX",
                "XXXXXXX",
                ".XXXXX.",
                ".XXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 8,
            title = "Tortoiseshell Spectacle Lock",
            chapterName = "Ch. 2 • Naty's Artisan Bakery & Grill",
            shapeName = "7×7 Bookworm Frames",
            accentBadge = "★★★★☆ Hard",
            host = CrewMember.SANDRA,
            hostTip = "Two interlocking lens rings! Beat this level to unlock Ally's 9×9 Daily Trophy Challenge!",
            cols = 7,
            rows = 7,
            difficultyStars = 4,
            difficultyLabel = "Hard",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "XXXXXXX",
                "XX.X.XX",
                "XXXXXXX",
                ".XXXXX.",
                "XXXXXXX",
                "XX.X.XX",
                ".XXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 9,
            title = "Master Baker's Wire Whisk",
            chapterName = "Ch. 2 • Naty's Artisan Bakery & Grill",
            shapeName = "7×7 Cross-Woven Whisk",
            accentBadge = "★★★★☆ Hard",
            host = CrewMember.NATY,
            hostTip = "43 dense cells on a 7×7 grid! Save your Power-Ups for the Broken AC Boss waiting on Level 10!",
            cols = 7,
            rows = 7,
            difficultyStars = 4,
            difficultyLabel = "Hard",
            startingHearts = 3,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXXX.",
                "XXXXXXX",
                "XXX.XXX",
                "XXXXXXX",
                ".XXXXX.",
                "..XXX..",
                "..XXX.."
            )
        ),
        LevelMeta(
            levelNumber = 10,
            title = "BOSS: Broken Air Conditioner (89°F)",
            chapterName = "Ch. 2 • Naty's Artisan Bakery & Grill",
            shapeName = "7×7 Overheated Condenser",
            accentBadge = "★★★★☆ Ch. 2 Boss",
            host = CrewMember.NATY,
            hostTip = "CHAPTER 2 BOSS GATE (2 Hearts Only)! Cool the 89°F Broken AC without making blocked mistakes to unlock Chapter 3!",
            cols = 7,
            rows = 7,
            difficultyStars = 4,
            difficultyLabel = "Hard Boss",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            bossType = BossType.BROKEN_AC,
            mask = listOf(
                ".XXXXX.",
                "XXXXXXX",
                "XXXXXXX",
                "XXX.XXX",
                "XXXXXXX",
                "XXXXXXX",
                ".XXXXX."
            )
        ),
        // CHAPTER 3: Ally's Gourmet Book Club Stack (Levels 11-15) — 8x8 High-Stakes (2 Hearts!)
        LevelMeta(
            levelNumber = 11,
            title = "Crinkle-Cut Fry Fortress (8×8)",
            chapterName = "Ch. 3 • Ally's Gourmet Book Club",
            shapeName = "8×8 Golden Fry Carton",
            accentBadge = "★★★★☆ Hard • 2 Hearts",
            host = CrewMember.ALLY,
            hostTip = "Welcome to Chapter 3! All 8×8 puzzles give you ONLY 2 Crochet Hearts—every single move requires pure strategic deduction!",
            cols = 8,
            rows = 8,
            difficultyStars = 4,
            difficultyLabel = "Hard",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "XX.XX.XX",
                "XXXXXXXX",
                "XXXXXXXX",
                ".XXXXXX.",
                "XXXXXXXX",
                "XXXXXXXX",
                ".XXXXXX.",
                "..XXXX.."
            )
        ),
        LevelMeta(
            levelNumber = 12,
            title = "Brioche Dough Labyrinth (8×8)",
            chapterName = "Ch. 3 • Ally's Gourmet Book Club",
            shapeName = "8×8 Baker's Pastry Wheel",
            accentBadge = "★★★★☆ Hard • 2 Hearts",
            host = CrewMember.NATY,
            hostTip = "5-cell winding yarn arrows interlock across the center hub. Unwind the outer spokes in exact reverse sequence!",
            cols = 8,
            rows = 8,
            difficultyStars = 4,
            difficultyLabel = "Hard",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "..XXXX..",
                ".XXXXXX.",
                "XXXXXXXX",
                "XXX..XXX",
                "XXX..XXX",
                "XXXXXXXX",
                ".XXXXXX.",
                "..XXXX.."
            )
        ),
        LevelMeta(
            levelNumber = 13,
            title = "Double-Decker Smash Tower (8×8)",
            chapterName = "Ch. 3 • Ally's Gourmet Book Club",
            shapeName = "8×8 Double Cheeseburger",
            accentBadge = "★★★★☆ Hard • 2 Hearts",
            host = CrewMember.SANDRA,
            hostTip = "58 cells packed into an 8×8 Double Cheeseburger! Beat this level to unlock 8×8 Endless Madness!",
            cols = 8,
            rows = 8,
            difficultyStars = 4,
            difficultyLabel = "Hard",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXXXX.",
                "XXXXXXXX",
                "XXXXXXXX",
                ".XXXXXX.",
                "XXXXXXXX",
                "XXXXXXXX",
                "XXXXXXXX",
                ".XXXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 14,
            title = "Midnight Book Club Vault (8×8)",
            chapterName = "Ch. 3 • Ally's Gourmet Book Club",
            shapeName = "8×8 Hardcover Archive",
            accentBadge = "★★★★★ Expert • 2 Hearts",
            host = CrewMember.SANDRA,
            hostTip = "60 interlocking cells! Trace every horizontal and vertical corridor twice before committing your tap.",
            cols = 8,
            rows = 8,
            difficultyStars = 5,
            difficultyLabel = "Expert",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXXXX.",
                "XXXXXXXX",
                "XXX..XXX",
                "XXXXXXXX",
                "XXXXXXXX",
                "XXX..XXX",
                "XXXXXXXX",
                ".XXXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 15,
            title = "BOSS: Messy Closet & Yarn Tornado",
            chapterName = "Ch. 3 • Ally's Gourmet Book Club",
            shapeName = "8×8 Overflowing Closet",
            accentBadge = "★★★★★ Ch. 3 Boss",
            host = CrewMember.ALLY,
            hostTip = "CHAPTER 3 BOSS GATE (350 HP, 2 Hearts)! Conquer the 8×8 Closet Tornado to earn entry into Chapter 4's 9×9 & 10×10 Grids!",
            cols = 8,
            rows = 8,
            difficultyStars = 5,
            difficultyLabel = "Expert Boss",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            bossType = BossType.LAUNDRY_YARN_TORNADO,
            mask = listOf(
                "XXXXXXXX",
                "XX.XX.XX",
                "XXXXXXXX",
                "X.XXXX.X",
                "XXXXXXXX",
                "XX.XX.XX",
                "XXXXXXXX",
                ".XXXXXX."
            )
        ),
        // CHAPTER 4: Grandmaster Madness: 9x9 & 10x10 Grids + Final Boss! (Levels 16-20)
        LevelMeta(
            levelNumber = 16,
            title = "Grandmaster Bookmark Star (9×9)",
            chapterName = "Ch. 4 • The Grey Area",
            shapeName = "9×9 Reading Star Matrix",
            accentBadge = "★★★★★ Grandmaster 9×9",
            host = CrewMember.SANDRA,
            hostTip = "Welcome to Chapter 4! 9×9 grid, 2 Hearts, and ultra-narrow interlocking arrows—only true tacticians make it here!",
            cols = 9,
            rows = 9,
            difficultyStars = 5,
            difficultyLabel = "Grandmaster",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "..XXXXX..",
                ".XXXXXXX.",
                "XXXXXXXXX",
                "XXXX.XXXX",
                "XXX...XXX",
                "XXXX.XXXX",
                "XXXXXXXXX",
                ".XXXXXXX.",
                "..XXXXX.."
            )
        ),
        LevelMeta(
            levelNumber = 17,
            title = "Brioche Crown Imperial (9×9)",
            chapterName = "Ch. 4 • The Grey Area",
            shapeName = "9×9 Royal Bakery Crown",
            accentBadge = "★★★★★ Grandmaster 9×9",
            host = CrewMember.NATY,
            hostTip = "75 tightly woven cells on a 9×9 grid! Chain 8x+ combos without a mistake to earn bonus Power-Up charges!",
            cols = 9,
            rows = 9,
            difficultyStars = 5,
            difficultyLabel = "Grandmaster",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "X..XXX..X",
                "XX.XXX.XX",
                "XXXXXXXXX",
                "XXXXXXXXX",
                ".XXXXXXX.",
                "XXXXXXXXX",
                "XXXXXXXXX",
                "XXXXXXXXX",
                ".XXXXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 18,
            title = "Sandra, Naty & Ally's 9×9 Banquet",
            chapterName = "Ch. 4 • The Grey Area",
            shapeName = "9×9 Triple-Crown Platter",
            accentBadge = "★★★★★ Grandmaster 9×9",
            host = CrewMember.ALLY,
            hostTip = "77 cells of pure strategic deduction! Beat this level to unlock the legendary 10×10 Grandmaster grids!",
            cols = 9,
            rows = 9,
            difficultyStars = 5,
            difficultyLabel = "Grandmaster",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                ".XXXXXXX.",
                "XXXXXXXXX",
                "XXXX.XXXX",
                "XXXXXXXXX",
                "XXX.X.XXX",
                "XXXXXXXXX",
                "XXXX.XXXX",
                "XXXXXXXXX",
                ".XXXXXXX."
            )
        ),
        LevelMeta(
            levelNumber = 19,
            title = "Colossal 10×10 Burger & Bakery Citadel",
            chapterName = "Ch. 4 • The Grey Area",
            shapeName = "10×10 Sky-High Burger Stack",
            accentBadge = "★★★★★ 10×10 Citadel",
            host = CrewMember.NATY,
            hostTip = "92 cells on a full 10×10 grid with only 1 starting keyhole! Every move unlocks the next link in the chain!",
            cols = 10,
            rows = 10,
            difficultyStars = 5,
            difficultyLabel = "Grandmaster",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            mask = listOf(
                "..XXXXXX..",
                ".XXXXXXXX.",
                "XXXXXXXXXX",
                "XXXXXXXXXX",
                "XXXX..XXXX",
                "XXXX..XXXX",
                "XXXXXXXXXX",
                "XXXXXXXXXX",
                ".XXXXXXXX.",
                "..XXXXXX.."
            )
        ),
        LevelMeta(
            levelNumber = 20,
            title = "FINAL BOSS: Messy House + Broken AC (10×10)",
            chapterName = "Ch. 4 • The Grey Area",
            shapeName = "10×10 Ultimate Burger & Chore Boss",
            accentBadge = "★★★★★ 10×10 Final Boss",
            host = CrewMember.SANDRA,
            hostTip = "THE 10×10 ULTIMATE FINALE (460 HP, 96 Cells, 2 Hearts)! Clean the Messy House AND repair the 92°F Broken AC to claim the Sesame Crown!",
            cols = 10,
            rows = 10,
            difficultyStars = 5,
            difficultyLabel = "Final Boss",
            startingHearts = 2,
            allowRayGuide = false,
            maxInitialOpenings = 1,
            bossType = BossType.ULTIMATE_CHORE_DUO,
            mask = listOf(
                ".XXXXXXXX.",
                "XXXXXXXXXX",
                "XXXXXXXXXX",
                "XX.XXXX.XX",
                "XXXXXXXXXX",
                "XXXXXXXXXX",
                "XX.XXXX.XX",
                "XXXXXXXXXX",
                "XXXXXXXXXX",
                ".XXXXXXXX."
            )
        )
    )

    val totalStoryLevels: Int = storyLevelMetas.size

    fun getStoryLevel(levelNumber: Int): LevelConfig {
        val meta = storyLevelMetas.find { it.levelNumber == levelNumber } ?: storyLevelMetas.first()
        return buildLevelFromMeta(meta)
    }

    fun getAllStoryLevelSummaries(): List<LevelConfig> {
        return storyLevelMetas.map { buildLevelFromMeta(it) }
    }

    /**
     * Launches a dedicated Turn-Based Boss Battle for any unlocked Boss!
     */
    fun getBossBattleLevel(bossType: BossType): LevelConfig {
        val (cols, rows, mask) = when (bossType) {
            BossType.MESSY_HOUSE -> Triple(
                7,
                7,
                listOf(
                    "..XXX..",
                    ".XXXXX.",
                    "XXXXXXX",
                    "XXXXXXX",
                    "XX.X.XX",
                    "XXXXXXX",
                    ".XXXXX."
                )
            )
            BossType.BROKEN_AC -> Triple(
                8,
                8,
                listOf(
                    ".XXXXXX.",
                    "XXXXXXXX",
                    "XX.XX.XX",
                    "XXXXXXXX",
                    "XXXXXXXX",
                    "XX.XX.XX",
                    "XXXXXXXX",
                    ".XXXXXX."
                )
            )
            BossType.LAUNDRY_YARN_TORNADO -> Triple(
                9,
                9,
                listOf(
                    "..XXXXX..",
                    ".XXXXXXX.",
                    "XXXXXXXXX",
                    "XX.XXX.XX",
                    "XXXXXXXXX",
                    "XX.XXX.XX",
                    "XXXXXXXXX",
                    ".XXXXXXX.",
                    "..XXXXX.."
                )
            )
            BossType.ULTIMATE_CHORE_DUO -> Triple(
                10,
                10,
                listOf(
                    ".XXXXXXXX.",
                    "XXXXXXXXXX",
                    "XXXXXXXXXX",
                    "XX.XXXX.XX",
                    "XXXXXXXXXX",
                    "XXXXXXXXXX",
                    "XX.XXXX.XX",
                    "XXXXXXXXXX",
                    "XXXXXXXXXX",
                    ".XXXXXXXX."
                )
            )
        }
        val activeCells = parseMask(mask, cols, rows)
        val arrows = generateGuaranteedSolvableArrows(
            activeCells = activeCells,
            cols = cols,
            rows = rows,
            seed = bossType.ordinal * 4999L + 777L,
            maxArrowLen = 5,
            maxInitialOpenings = 1
        )
        val pattern = buildBurgerColorPattern(activeCells, rows)
        return LevelConfig(
            levelNumber = 300 + bossType.ordinal,
            title = "BOSS: ${bossType.bossName}",
            chapterName = "Turn-Based Boss Arena • ${cols}×$rows Grid",
            shapeName = bossType.subtitle,
            accentBadge = "★★★★★ Boss Arena",
            host = CrewMember.SANDRA,
            hostTip = "Sandra: ${bossType.attackDescription}",
            gridCols = cols,
            gridRows = rows,
            arrows = arrows,
            stitchPattern = pattern,
            difficultyStars = 5,
            difficultyLabel = "Boss Arena",
            startingHearts = 2,
            parMoves = arrows.size,
            allowRayGuide = false,
            bossType = bossType
        )
    }

    fun getDailyChallengeLevel(daySeed: Long): LevelConfig {
        val cols = 9
        val rows = 9
        val mask = listOf(
            ".XXXXXXX.",
            "XXXXXXXXX",
            "XX.XXX.XX",
            "XXXXXXXXX",
            "XXXXXXXXX",
            "XXXXXXXXX",
            "XX.XXX.XX",
            "XXXXXXXXX",
            ".XXXXXXX."
        )
        val activeCells = parseMask(mask, cols, rows)
        val arrows = generateGuaranteedSolvableArrows(
            activeCells = activeCells,
            cols = cols,
            rows = rows,
            seed = daySeed * 9973L + 42L,
            maxArrowLen = 5,
            maxInitialOpenings = 1
        )
        val pattern = buildBurgerColorPattern(activeCells, rows)
        return LevelConfig(
            levelNumber = 100,
            title = "Ally's Daily 9×9 Burger Trophy",
            chapterName = "Special • Daily Master Challenge",
            shapeName = "Golden Deluxe Crochet Burger",
            accentBadge = "★★★★★ Daily 9×9",
            host = CrewMember.ALLY,
            hostTip = "Beat today's 9×9 Daily Challenge (2 Hearts, 1 Keyhole, No Ray Guide) to earn a Golden Trophy!",
            gridCols = cols,
            gridRows = rows,
            arrows = arrows,
            stitchPattern = pattern,
            difficultyStars = 5,
            difficultyLabel = "Daily Master",
            startingHearts = 2,
            parMoves = arrows.size,
            allowRayGuide = false,
            isDailyChallenge = true
        )
    }

    fun generateEndlessMadnessLevel(gridSize: Int, madnessRound: Int, seed: Long): LevelConfig {
        val cols = gridSize.coerceIn(5, 10)
        val rows = gridSize.coerceIn(5, 10)
        val activeCells = mutableSetOf<GridCell>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val isCorner = (r == 0 || r == rows - 1) && (c == 0 || c == cols - 1)
                if (!isCorner) {
                    activeCells.add(GridCell(c, r))
                }
            }
        }
        val hosts = CrewMember.entries
        val host = hosts[madnessRound % hosts.size]
        val arrows = generateGuaranteedSolvableArrows(
            activeCells = activeCells,
            cols = cols,
            rows = rows,
            seed = seed + madnessRound * 1337L,
            maxArrowLen = if (cols >= 7) 5 else 4,
            maxInitialOpenings = 1
        )
        val pattern = buildBurgerColorPattern(activeCells, rows)
        val diffStars = when {
            cols <= 6 -> 3
            cols <= 8 -> 4
            else -> 5
        }
        return LevelConfig(
            levelNumber = 200 + madnessRound,
            title = "Madness Recipe #$madnessRound (${cols}×$rows)",
            chapterName = "Endless • Sandra's Strategic Madness",
            shapeName = "${cols}×$rows Interlocking Weave",
            accentBadge = "Endless ${cols}×$rows",
            host = host,
            hostTip = "${host.displayName}: Round #$madnessRound (${cols}×$rows)! Only 1 keyhole opening starts unlocked—unravel the chain with precision!",
            gridCols = cols,
            gridRows = rows,
            arrows = arrows,
            stitchPattern = pattern,
            difficultyStars = diffStars,
            difficultyLabel = if (cols >= 9) "Grandmaster" else "Hard",
            startingHearts = if (cols >= 8) 2 else 3,
            parMoves = arrows.size,
            allowRayGuide = false,
            isEndlessMode = true
        )
    }

    private fun buildLevelFromMeta(meta: LevelMeta): LevelConfig {
        val activeCells = parseMask(meta.mask, meta.cols, meta.rows)
        val maxLen = when {
            meta.levelNumber == 1 -> 3
            meta.levelNumber <= 5 -> 4
            else -> 5
        }
        val arrows = generateGuaranteedSolvableArrows(
            activeCells = activeCells,
            cols = meta.cols,
            rows = meta.rows,
            seed = meta.levelNumber * 7919L + 313L,
            maxArrowLen = maxLen,
            maxInitialOpenings = meta.maxInitialOpenings
        )
        val pattern = buildBurgerColorPattern(activeCells, meta.rows)
        return LevelConfig(
            levelNumber = meta.levelNumber,
            title = meta.title,
            chapterName = meta.chapterName,
            shapeName = meta.shapeName,
            accentBadge = meta.accentBadge,
            host = meta.host,
            hostTip = meta.hostTip,
            gridCols = meta.cols,
            gridRows = meta.rows,
            arrows = arrows,
            stitchPattern = pattern,
            difficultyStars = meta.difficultyStars,
            difficultyLabel = meta.difficultyLabel,
            startingHearts = meta.startingHearts,
            parMoves = arrows.size,
            allowRayGuide = meta.allowRayGuide,
            bossType = meta.bossType
        )
    }

    private fun parseMask(mask: List<String>, cols: Int, rows: Int): Set<GridCell> {
        val set = LinkedHashSet<GridCell>()
        for (r in 0 until rows) {
            val line = mask.getOrNull(r) ?: continue
            for (c in 0 until cols) {
                if (line.getOrNull(c) == 'X') {
                    set.add(GridCell(c, r))
                }
            }
        }
        return set
    }

    private fun buildBurgerColorPattern(
        cells: Set<GridCell>,
        totalRows: Int
    ): Map<GridCell, YarnArrowColor> {
        val result = HashMap<GridCell, YarnArrowColor>(cells.size)
        for (cell in cells) {
            val normalizedRow = if (totalRows <= 1) 0f else cell.row.toFloat() / (totalRows - 1).toFloat()
            val color = when {
                normalizedRow < 0.22f -> YarnArrowColor.SESAME_GOLD
                normalizedRow < 0.36f -> YarnArrowColor.LETTUCE_GREEN
                normalizedRow < 0.50f -> YarnArrowColor.KETCHUP_RED
                normalizedRow < 0.65f -> YarnArrowColor.SESAME_GOLD
                normalizedRow < 0.82f -> YarnArrowColor.BRIOCHE_BROWN
                else -> YarnArrowColor.SESAME_GOLD
            }
            result[cell] = color
        }
        return result
    }

    /**
     * Strategic Interlocking Reverse-Peeling Generator:
     * - Guarantees 100% mathematical solvability without guessing.
     * - Limits the number of initially unblocked arrows to [maxInitialOpenings] (1 on Challenging/Hard/Grandmaster levels!)
     *   by scoring candidate rays that pass directly through ALREADY-PEELED cells ([peeledCells] and [lastPeeledCells]).
     * - Grows multi-segment serpentine arrows (3–5 cells with 90-degree bends) that cross interior lanes.
     */
    fun generateGuaranteedSolvableArrows(
        activeCells: Set<GridCell>,
        cols: Int,
        rows: Int,
        seed: Long,
        maxArrowLen: Int,
        maxInitialOpenings: Int = 1
    ): List<PuzzleArrow> {
        val rng = Random(seed)
        val remaining = LinkedHashSet<GridCell>(activeCells)
        val peeledCells = HashSet<GridCell>()
        var lastPeeledCells: Set<GridCell> = emptySet()
        val peeledArrows = mutableListOf<PuzzleArrow>()
        val palette = YarnArrowColor.entries
        var nextId = 1

        while (remaining.isNotEmpty()) {
            val candidates = mutableListOf<Pair<GridCell, Direction>>()
            for (cell in remaining) {
                for (dir in Direction.entries) {
                    if (isRayClearOfRemaining(cell, dir, remaining, cols, rows)) {
                        candidates.add(cell to dir)
                    }
                }
            }

            // Score each candidate to enforce deep sequential blocking chains!
            val isOpeningArrow = peeledArrows.size < maxInitialOpenings
            var bestScore = Int.MIN_VALUE
            val topCandidates = mutableListOf<Pair<GridCell, Direction>>()

            for (candidate in candidates) {
                val (cell, dir) = candidate
                val rayStats = measureRayDependencies(
                    start = cell,
                    dir = dir,
                    peeledCells = peeledCells,
                    lastPeeledCells = lastPeeledCells,
                    cols = cols,
                    rows = rows
                )
                val hasTailNeighbor = cell.step(dir.opposite()) in remaining

                val score = if (isOpeningArrow) {
                    // For the 1 (or 2) initial keyhole arrows: prefer arrows with room for a multi-cell body
                    // and a longer ray across the board rather than a 1-cell stub.
                    (if (hasTailNeighbor) 400 else 0) + (rayStats.stepsToEdge * 35)
                } else {
                    // For all subsequent arrows: strongly require the exit ray to pass through
                    // previously peeled arrows so this arrow is LOCKED at the start of the puzzle!
                    var s = 0
                    if (rayStats.lastPeeledHits > 0) {
                        s += 2500 + rayStats.lastPeeledHits * 350
                    } else if (rayStats.totalPeeledHits > 0) {
                        s += 1100 + rayStats.totalPeeledHits * 220
                    } else {
                        // Would be an extra unblocked arrow at puzzle start—heavily penalize!
                        s -= 5000
                    }
                    if (hasTailNeighbor) s += 260
                    s += rayStats.stepsToEdge * 30
                    s
                }

                if (score > bestScore) {
                    bestScore = score
                    topCandidates.clear()
                    topCandidates.add(candidate)
                } else if (score >= bestScore - 120) {
                    topCandidates.add(candidate)
                }
            }

            val chosen = topCandidates[rng.nextInt(topCandidates.size)]
            val headCell = chosen.first
            val headDir = chosen.second

            val desiredLen = if (maxArrowLen <= 1) {
                1
            } else {
                val roll = rng.nextFloat()
                when {
                    roll < 0.04f -> 2
                    roll < 0.38f -> minOf(3, maxArrowLen)
                    roll < 0.78f -> minOf(4, maxArrowLen)
                    else -> maxArrowLen
                }
            }

            val chainFromHeadToTail = mutableListOf(headCell)
            var current = headCell
            var lastStepDir = headDir.opposite()
            var firstStepTaken = false

            while (chainFromHeadToTail.size < desiredLen) {
                val candidateDirs = if (!firstStepTaken) {
                    listOf(headDir.opposite())
                } else {
                    // Prefer 90-degree bends (L, U, Z hooks) so arrow tails wrap across neighboring lanes!
                    val perpendiculars = Direction.entries.filter {
                        it != lastStepDir && it != lastStepDir.opposite()
                    }.shuffled(rng)
                    perpendiculars + listOf(lastStepDir)
                }

                var nextNeighbor: GridCell? = null
                var chosenStepDir: Direction? = null
                for (dir in candidateDirs) {
                    val neighbor = current.step(dir)
                    if (neighbor in remaining && neighbor !in chainFromHeadToTail) {
                        nextNeighbor = neighbor
                        chosenStepDir = dir
                        break
                    }
                }

                if (nextNeighbor == null || chosenStepDir == null) {
                    break
                }
                chainFromHeadToTail.add(nextNeighbor)
                current = nextNeighbor
                lastStepDir = chosenStepDir
                firstStepTaken = true
            }

            val orderedCells = chainFromHeadToTail.reversed()
            val arrowCellSet = orderedCells.toSet()
            remaining.removeAll(arrowCellSet)
            peeledCells.addAll(arrowCellSet)
            lastPeeledCells = arrowCellSet

            val color = assignHarmoniousColor(headCell, rows, nextId, palette, rng)
            peeledArrows.add(
                PuzzleArrow(
                    id = nextId++,
                    cells = orderedCells,
                    headDirection = headDir,
                    yarnColor = color
                )
            )
        }

        return peeledArrows
    }

    private data class RayDependencyStats(
        val stepsToEdge: Int,
        val totalPeeledHits: Int,
        val lastPeeledHits: Int
    )

    private fun measureRayDependencies(
        start: GridCell,
        dir: Direction,
        peeledCells: Set<GridCell>,
        lastPeeledCells: Set<GridCell>,
        cols: Int,
        rows: Int
    ): RayDependencyStats {
        var steps = 0
        var totalHits = 0
        var lastHits = 0
        var cursor = start.step(dir)
        while (cursor.col in 0 until cols && cursor.row in 0 until rows) {
            steps++
            if (cursor in peeledCells) totalHits++
            if (cursor in lastPeeledCells) lastHits++
            cursor = cursor.step(dir)
        }
        return RayDependencyStats(
            stepsToEdge = steps,
            totalPeeledHits = totalHits,
            lastPeeledHits = lastHits
        )
    }

    private fun assignHarmoniousColor(
        headCell: GridCell,
        rows: Int,
        id: Int,
        palette: List<YarnArrowColor>,
        rng: Random
    ): YarnArrowColor {
        val rowRatio = if (rows <= 1) 0f else headCell.row.toFloat() / (rows - 1).toFloat()
        return if (rng.nextFloat() < 0.65f) {
            when {
                rowRatio < 0.22f -> YarnArrowColor.SESAME_GOLD
                rowRatio < 0.38f -> YarnArrowColor.LETTUCE_GREEN
                rowRatio < 0.52f -> YarnArrowColor.KETCHUP_RED
                rowRatio < 0.68f -> YarnArrowColor.NATY_TEAL
                rowRatio < 0.82f -> YarnArrowColor.BRIOCHE_BROWN
                else -> YarnArrowColor.ALLY_PINK
            }
        } else {
            palette[id % palette.size]
        }
    }

    private fun isRayClearOfRemaining(
        start: GridCell,
        dir: Direction,
        remaining: Set<GridCell>,
        cols: Int,
        rows: Int
    ): Boolean {
        var cursor = start.step(dir)
        while (cursor.col in 0 until cols && cursor.row in 0 until rows) {
            if (cursor in remaining) return false
            cursor = cursor.step(dir)
        }
        return true
    }

    fun findFirstBlockingCell(
        arrow: PuzzleArrow,
        allActiveArrows: List<PuzzleArrow>,
        cols: Int,
        rows: Int
    ): GridCell? {
        val occupiedByOthers = HashSet<GridCell>()
        for (other in allActiveArrows) {
            if (other.id != arrow.id) {
                occupiedByOthers.addAll(other.cells)
            }
        }
        var cursor = arrow.headCell.step(arrow.headDirection)
        while (cursor.col in 0 until cols && cursor.row in 0 until rows) {
            if (cursor in occupiedByOthers) {
                return cursor
            }
            cursor = cursor.step(arrow.headDirection)
        }
        return null
    }

    fun countCurrentlyClearArrows(
        arrows: List<PuzzleArrow>,
        cols: Int,
        rows: Int
    ): Int {
        return arrows.count { candidate ->
            findFirstBlockingCell(candidate, arrows, cols, rows) == null
        }
    }
}
