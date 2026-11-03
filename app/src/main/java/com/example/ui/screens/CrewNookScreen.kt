package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.PlayerProgressState
import com.example.model.CrewMember
import com.example.ui.components.CrewAvatarBadge

@Composable
fun CrewNookScreen(
    progressState: PlayerProgressState,
    onToggleYarnRibs: (Boolean) -> Unit,
    onToggleRayGuide: (Boolean) -> Unit,
    onToggleSfx: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onSetDarkThemeOverride: (Boolean?) -> Unit,
    onResetProgress: () -> Unit,
    onBackToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBackToPlay() }

    var showResetConfirm by remember { mutableStateOf(false) }

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
                Text(
                    text = "Sandra, Naty & Ally's Cozy Book & Bakery Nook",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Where crochet hooks, sizzling burgers, warm pastries, good books, and Messy House / Broken AC boss battles meet!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Player Lifetime Stats Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadgeItem(
                            value = "${progressState.totalArrowsCleared}",
                            label = "Arrows Freed"
                        )
                        StatBadgeItem(
                            value = "${progressState.bossesDefeated}",
                            label = "Bosses Tamed"
                        )
                        StatBadgeItem(
                            value = "${progressState.bestComboStreak}x",
                            label = "Best Streak"
                        )
                        StatBadgeItem(
                            value = "${progressState.totalScore}",
                            label = "Total Score"
                        )
                    }
                }
            }

            // Meet Sandra (featuring her real photo!), Naty & Ally
            items(CrewMember.entries) { member ->
                CrewMemberProfileCard(member = member)
            }

            // Sound, Music & Board Settings
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Audio, Narrow Arrows & Reading Lamp Settings",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Addictive Ascending Pluck SFX",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Plays musical pentatonic plucks that rise in pitch with your combo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = progressState.sfxEnabled,
                                onCheckedChange = onToggleSfx,
                                modifier = Modifier.testTag("toggle_sfx_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Calming Lo-Fi Music Box Loop",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Gentle ambient acoustic chord progression while you solve puzzles",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = progressState.calmingMusicEnabled,
                                onCheckedChange = onToggleMusic,
                                modifier = Modifier.testTag("toggle_music_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Crochet Stitch Ribs on Narrow Arrows",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Adds subtle yarn ribs while keeping the narrow Arrow Puzzle look",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = progressState.showYarnStitchRibs,
                                onCheckedChange = onToggleYarnRibs,
                                modifier = Modifier.testTag("toggle_yarn_ribs_switch")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Show Exit Ray Guide Dots",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Displays faint trajectory dots ahead of every arrowhead",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = progressState.showRayGuide,
                                onCheckedChange = onToggleRayGuide,
                                modifier = Modifier.testTag("toggle_ray_guide_switch")
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Cafe Lighting Theme",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = progressState.darkThemeOverride == null,
                                    onClick = { onSetDarkThemeOverride(null) },
                                    label = { Text("Auto") },
                                    modifier = Modifier.testTag("theme_auto_chip")
                                )
                                FilterChip(
                                    selected = progressState.darkThemeOverride == false,
                                    onClick = { onSetDarkThemeOverride(false) },
                                    label = { Text("Warm Linen") },
                                    modifier = Modifier.testTag("theme_light_chip")
                                )
                                FilterChip(
                                    selected = progressState.darkThemeOverride == true,
                                    onClick = { onSetDarkThemeOverride(true) },
                                    label = { Text("Night Book Lamp") },
                                    modifier = Modifier.testTag("theme_dark_chip")
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showResetConfirm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_progress_button")
                        ) {
                            Text("Reset All Story Progress")
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Sandra's Madness Progress?") },
            text = {
                Text("This will reset your completed story levels and stats back to Level 1.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetConfirm = false
                        onResetProgress()
                    },
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatBadgeItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CrewMemberProfileCard(member: CrewMember) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("crew_card_${member.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CrewAvatarBadge(member = member, size = 68.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = member.badgeColor
                )
                Text(
                    text = member.roleTitle,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = member.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = member.badgeColor.copy(alpha = 0.14f),
                    shape = CircleShape
                ) {
                    Text(
                        text = member.specialty,
                        style = MaterialTheme.typography.labelMedium,
                        color = member.badgeColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
