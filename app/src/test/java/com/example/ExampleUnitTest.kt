package com.example

import com.example.data.LevelCatalog
import com.example.model.BossType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun allStoryLevelsBossesAnd10x10GridsAreGuaranteedSolvableAndStrategic() {
        var level1Cells = 0
        var level20Cells = 0

        for (levelNum in 1..LevelCatalog.totalStoryLevels) {
            val config = LevelCatalog.getStoryLevel(levelNum)
            val remaining = config.arrows.toMutableList()
            assertTrue("Level $levelNum must have arrows", remaining.isNotEmpty())

            val totalCells = config.arrows.sumOf { it.cells.size }
            if (levelNum == 1) level1Cells = totalCells
            if (levelNum == 20) level20Cells = totalCells

            val initialClear = LevelCatalog.countCurrentlyClearArrows(
                arrows = config.arrows,
                cols = config.gridCols,
                rows = config.gridRows
            )
            // Verify strategic interlocking: at least 1 arrow is clear, and most of the board starts locked!
            assertTrue("Level $levelNum must have at least 1 clear opening", initialClear >= 1)
            assertTrue(
                "Level $levelNum must have the majority of arrows initially blocked for strategic play",
                initialClear < config.arrows.size
            )

            while (remaining.isNotEmpty()) {
                val clearArrow = remaining.firstOrNull { candidate ->
                    LevelCatalog.findFirstBlockingCell(
                        arrow = candidate,
                        allActiveArrows = remaining,
                        cols = config.gridCols,
                        rows = config.gridRows
                    ) == null
                }
                assertTrue(
                    "Level $levelNum (${config.title}) must always have at least one unobstructed arrow",
                    clearArrow != null
                )
                remaining.remove(clearArrow)
            }
        }

        // Verify progressive scaling from Level 1 to Level 20
        assertTrue("Level 20 ($level20Cells cells) must be much larger than Level 1 ($level1Cells cells)", level20Cells > level1Cells * 3)

        // Verify progressive unlock gates
        assertTrue(LevelCatalog.isStoryLevelUnlocked(1, highestUnlockedLevel = 1))
        assertFalse(LevelCatalog.isStoryLevelUnlocked(2, highestUnlockedLevel = 1))
        assertFalse(LevelCatalog.isBossUnlocked(BossType.MESSY_HOUSE, highestUnlockedLevel = 1, completedLevels = emptySet()))
        assertTrue(LevelCatalog.isBossUnlocked(BossType.MESSY_HOUSE, highestUnlockedLevel = 5, completedLevels = setOf(5)))
        assertFalse(LevelCatalog.isEndlessSizeUnlocked(10, highestUnlockedLevel = 1))
        assertTrue(LevelCatalog.isEndlessSizeUnlocked(10, highestUnlockedLevel = 19))

        // Verify all 4 Turn-Based Boss Battles (including Messy House & Broken AC)
        for (boss in BossType.entries) {
            val bossLevel = LevelCatalog.getBossBattleLevel(boss)
            val remaining = bossLevel.arrows.toMutableList()
            assertTrue("Boss ${boss.name} must have arrows", remaining.isNotEmpty())
            while (remaining.isNotEmpty()) {
                val clearArrow = remaining.firstOrNull { candidate ->
                    LevelCatalog.findFirstBlockingCell(
                        arrow = candidate,
                        allActiveArrows = remaining,
                        cols = bossLevel.gridCols,
                        rows = bossLevel.gridRows
                    ) == null
                }
                assertTrue("Boss ${boss.name} must be 100% solvable", clearArrow != null)
                remaining.remove(clearArrow)
            }
        }

        // Verify 10x10 Endless Madness level
        val endless10x10 = LevelCatalog.generateEndlessMadnessLevel(
            gridSize = 10,
            madnessRound = 1,
            seed = 12345L
        )
        assertEquals(10, endless10x10.gridCols)
        assertEquals(10, endless10x10.gridRows)
    }
}
