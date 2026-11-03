package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LevelCatalog
import com.example.data.PlayerProgressState
import com.example.model.AccentCategory
import com.example.model.ArrowPinStyle
import com.example.model.BoardBackgroundStyle
import com.example.model.BurgerYarnLayer
import com.example.ui.components.CrochetBurgerStackCanvas

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CafeKitchenScreen(
    progressState: PlayerProgressState,
    onToggleLayerInStack: (String) -> Unit,
    onSelectArrowPinStyle: (ArrowPinStyle) -> Unit,
    onSelectBoardBgStyle: (BoardBackgroundStyle) -> Unit,
    onBackToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToPlay() }

    var selectedCategory by remember { mutableStateOf<AccentCategory?>(null) }

    val allCollectibles = LevelCatalog.burgerCollectibles
    val activeLayers = remember(progressState.activeBurgerStackIds, progressState.highestUnlockedLevel) {
        allCollectibles.filter {
            it.unlockedAtLevel <= progressState.highestUnlockedLevel &&
                it.id in progressState.activeBurgerStackIds
        }.ifEmpty {
            listOf(allCollectibles.first())
        }
    }

    val filteredCollectibles = remember(selectedCategory) {
        if (selectedCategory == null) {
            allCollectibles
        } else {
            allCollectibles.filter { it.category == selectedCategory }
        }
    }

    val totalStitches = activeLayers.sumOf { it.stitchCount }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Custom Game Asset Collection Selector (Arrow/Pin Assets & Background Mats)
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    ),
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Crochet & Burger Game Asset Studio",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Customize your narrow Arrow/Pin assets and woven Hamburger Mat background!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )

                        Text(
                            text = "Narrow Arrow / Pin Asset Style:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ArrowPinStyle.entries.forEach { pinStyle ->
                                FilterChip(
                                    selected = progressState.arrowPinStyle == pinStyle,
                                    onClick = { onSelectArrowPinStyle(pinStyle) },
                                    label = { Text(pinStyle.title) },
                                    modifier = Modifier.testTag("pin_style_${pinStyle.name.lowercase()}")
                                )
                            }
                        }

                        Text(
                            text = "Board Mat Background Asset:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BoardBackgroundStyle.entries.forEach { bgStyle ->
                                FilterChip(
                                    selected = progressState.boardBgStyle == bgStyle,
                                    onClick = { onSelectBoardBgStyle(bgStyle) },
                                    label = { Text(bgStyle.title) },
                                    modifier = Modifier.testTag("bg_style_${bgStyle.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }

            // 2. Interactive Crochet Burger, Bakery & Book Stacker
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Crochet Burger, Bakery & Book Stacker",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${activeLayers.size} Active Layers • $totalStitches Total Yarn Stitches",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CrochetBurgerStackCanvas(
                            layers = activeLayers,
                            modifier = Modifier.testTag("burger_stack_canvas")
                        )
                        Text(
                            text = "Toggle unlocked burger layers, baked pastries & reading accents below!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All (${allCollectibles.size})") },
                        modifier = Modifier.testTag("filter_all_chip")
                    )
                    AccentCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label) },
                            modifier = Modifier.testTag("filter_${cat.name.lowercase()}_chip")
                        )
                    }
                }
            }

            items(filteredCollectibles, key = { it.id }) { item ->
                val isUnlocked = item.unlockedAtLevel <= progressState.highestUnlockedLevel
                val isEquipped = item.id in progressState.activeBurgerStackIds
                CollectibleLayerCard(
                    item = item,
                    isUnlocked = isUnlocked,
                    isEquipped = isEquipped,
                    onToggleEquipped = { onToggleLayerInStack(item.id) }
                )
            }
        }
    }
}

@Composable
private fun CollectibleLayerCard(
    item: BurgerYarnLayer,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    onToggleEquipped: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("collectible_card_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.primaryColor),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Unlocked",
                        tint = item.accentColor
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = item.accentColor
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    ) {
                        Text(
                            text = item.category.label,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        color = item.craftedBy.badgeColor.copy(alpha = 0.14f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "By ${item.craftedBy.displayName} • ${item.stitchCount} Stitches",
                            style = MaterialTheme.typography.labelMedium,
                            color = item.craftedBy.badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (isUnlocked) {
                Switch(
                    checked = isEquipped,
                    onCheckedChange = { onToggleEquipped() },
                    modifier = Modifier.testTag("equip_switch_${item.id}")
                )
            } else {
                Text(
                    text = "Lv. ${item.unlockedAtLevel}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
