package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppDestination
import com.example.ui.GameViewModel
import com.example.ui.screens.CafeKitchenScreen
import com.example.ui.screens.ChaptersScreen
import com.example.ui.screens.CrewNookScreen
import com.example.ui.screens.PuzzlePlayScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val gameViewModel: GameViewModel = viewModel()
            val progressState by gameViewModel.progressState.collectAsStateWithLifecycle()

            val systemDark = isSystemInDarkTheme()
            val effectiveDark = progressState.darkThemeOverride ?: systemDark

            MyApplicationTheme(darkTheme = effectiveDark) {
                SandrasMadnessApp(
                    viewModel = gameViewModel,
                    isDarkTheme = effectiveDark
                )
            }
        }
    }
}

private data class NavTabItem(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

private val navItems = listOf(
    NavTabItem(
        destination = AppDestination.PLAY,
        label = "Play",
        icon = Icons.Default.Extension,
        testTag = "nav_tab_play"
    ),
    NavTabItem(
        destination = AppDestination.CHAPTERS,
        label = "Bosses & 10×10",
        icon = Icons.Default.AutoStories,
        testTag = "nav_tab_chapters"
    ),
    NavTabItem(
        destination = AppDestination.CAFE_KITCHEN,
        label = "Assets & Burger",
        icon = Icons.Default.BakeryDining,
        testTag = "nav_tab_kitchen"
    ),
    NavTabItem(
        destination = AppDestination.CREW_NOOK,
        label = "Sandra & Crew",
        icon = Icons.Default.Groups,
        testTag = "nav_tab_crew"
    )
)

@Composable
fun SandrasMadnessApp(
    viewModel: GameViewModel,
    isDarkTheme: Boolean
) {
    val boardState by viewModel.boardState.collectAsStateWithLifecycle()
    val progressState by viewModel.progressState.collectAsStateWithLifecycle()
    val currentSong by viewModel.currentSongFlow.collectAsStateWithLifecycle()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavRail = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useNavRail) {
                    NavigationBar {
                        navItems.forEach { item ->
                            NavigationBarItem(
                                selected = boardState.currentTab == item.destination,
                                onClick = { viewModel.selectTab(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavRail) {
                    NavigationRail {
                        navItems.forEach { item ->
                            NavigationRailItem(
                                selected = boardState.currentTab == item.destination,
                                onClick = { viewModel.selectTab(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                when (boardState.currentTab) {
                    AppDestination.PLAY -> PuzzlePlayScreen(
                        boardState = boardState,
                        progressState = progressState,
                        currentSong = currentSong,
                        isDarkTheme = isDarkTheme,
                        onCellTapped = viewModel::onCellOrArrowTapped,
                        onActivatePowerUp = viewModel::activatePowerUp,
                        onRequestHint = viewModel::requestNatyHint,
                        onStitchHeart = viewModel::stitchExtraHeart,
                        onRestartLevel = viewModel::restartCurrentLevel,
                        onNextLevel = viewModel::advanceToNextLevel,
                        onSelectStoryLevel = viewModel::loadStoryLevel,
                        onStartBossFight = viewModel::startBossBattle,
                        onSwitchSong = viewModel::switchSongNow,
                        onTriggerJerkyShout = viewModel::triggerSandraJerkyVoice,
                        onToggleRayGuide = viewModel::toggleRayGuide,
                        onToggleSfx = viewModel::toggleSfx,
                        onToggleMusic = viewModel::toggleCalmingMusic,
                        modifier = Modifier.weight(1f)
                    )
                    AppDestination.CHAPTERS -> ChaptersScreen(
                        progressState = progressState,
                        selectedEndlessSize = boardState.endlessGridSize,
                        onSelectStoryLevel = viewModel::loadStoryLevel,
                        onStartBossBattle = viewModel::startBossBattle,
                        onStartDailyChallenge = viewModel::startDailyChallenge,
                        onStartEndlessMode = viewModel::startEndlessMadness,
                        onBackToPlay = { viewModel.selectTab(AppDestination.PLAY) },
                        modifier = Modifier.weight(1f)
                    )
                    AppDestination.CAFE_KITCHEN -> CafeKitchenScreen(
                        progressState = progressState,
                        onToggleLayerInStack = viewModel::toggleCollectibleInStack,
                        onSelectArrowPinStyle = viewModel::setArrowPinStyle,
                        onSelectBoardBgStyle = viewModel::setBoardBgStyle,
                        onBackToPlay = { viewModel.selectTab(AppDestination.PLAY) },
                        modifier = Modifier.weight(1f)
                    )
                    AppDestination.CREW_NOOK -> CrewNookScreen(
                        progressState = progressState,
                        onToggleYarnRibs = viewModel::toggleYarnStitchRibs,
                        onToggleRayGuide = viewModel::toggleRayGuide,
                        onToggleSfx = viewModel::toggleSfx,
                        onToggleMusic = viewModel::toggleCalmingMusic,
                        onSetDarkThemeOverride = viewModel::setDarkThemeOverride,
                        onResetProgress = viewModel::resetProgress,
                        onBackToPlay = { viewModel.selectTab(AppDestination.PLAY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
