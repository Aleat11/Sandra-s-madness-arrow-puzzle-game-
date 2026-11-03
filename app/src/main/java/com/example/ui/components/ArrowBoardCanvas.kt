package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.ArrowPinStyle
import com.example.model.BoardBackgroundStyle
import com.example.model.GridCell
import com.example.model.PuzzleArrow
import com.example.model.YarnArrowColor
import com.example.ui.BlockedArrowAnim
import com.example.ui.ExitingArrowAnim
import com.example.ui.LaunchExplosionAnim
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ArrowBoardCanvas(
    gridCols: Int,
    gridRows: Int,
    arrows: List<PuzzleArrow>,
    exitingArrows: List<ExitingArrowAnim>,
    launchExplosions: List<LaunchExplosionAnim>,
    clearedCells: Set<GridCell>,
    bossHazardCells: Set<GridCell>,
    stitchPattern: Map<GridCell, YarnArrowColor>,
    comboStreak: Int,
    hintedArrowId: Int?,
    xrayArrowIds: Set<Int>,
    isSpatulaModeActive: Boolean,
    blockedAnim: BlockedArrowAnim?,
    showRayGuide: Boolean,
    showYarnStitchRibs: Boolean,
    arrowPinStyle: ArrowPinStyle,
    boardBgStyle: BoardBackgroundStyle,
    isDarkTheme: Boolean,
    onCellTapped: (GridCell) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "board_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hint_pulse"
    )

    var animTickMs by remember { mutableFloatStateOf(0f) }
    val hasActiveAnims = exitingArrows.isNotEmpty() || blockedAnim != null || launchExplosions.isNotEmpty()
    LaunchedEffect(hasActiveAnims) {
        if (hasActiveAnims) {
            val startNs = withFrameNanos { it }
            while (exitingArrows.isNotEmpty() || blockedAnim != null || launchExplosions.isNotEmpty()) {
                val nowNs = withFrameNanos { it }
                animTickMs = (nowNs - startNs) / 1_000_000f
            }
        }
    }

    val aspect = gridCols.toFloat() / gridRows.toFloat()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspect.coerceIn(0.85f, 1.18f)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .testTag("arrow_puzzle_board_canvas")
                .pointerInput(gridCols, gridRows, arrows, isSpatulaModeActive) {
                    detectTapGestures { tapOffset ->
                        val boardPadding = 16.dp.toPx()
                        val usableW = size.width - boardPadding * 2f
                        val usableH = size.height - boardPadding * 2f
                        val cellSize = min(usableW / gridCols, usableH / gridRows)
                        val gridW = cellSize * gridCols
                        val gridH = cellSize * gridRows
                        val originX = (size.width - gridW) / 2f
                        val originY = (size.height - gridH) / 2f

                        val col = ((tapOffset.x - originX) / cellSize).toInt()
                        val row = ((tapOffset.y - originY) / cellSize).toInt()
                        if (col in 0 until gridCols && row in 0 until gridRows) {
                            onCellTapped(GridCell(col, row))
                        }
                    }
                }
        ) {
            val unusedTick = animTickMs
            val nowMs = System.currentTimeMillis()

            val boardPadding = 16.dp.toPx()
            val usableW = size.width - boardPadding * 2f
            val usableH = size.height - boardPadding * 2f
            val cellSize = min(usableW / gridCols, usableH / gridRows)
            val gridW = cellSize * gridCols
            val gridH = cellSize * gridRows
            val originX = (size.width - gridW) / 2f
            val originY = (size.height - gridH) / 2f

            // 1. Custom Crochet + Hamburger Mat Background Asset
            val matBgColor = if (isDarkTheme) boardBgStyle.darkBg else boardBgStyle.lightBg
            val isComboFever = comboStreak >= 3
            val matBorderColor = when {
                isSpatulaModeActive -> Color(0xFFF59E0B)
                isComboFever -> Color(0xFFEC4899).copy(alpha = 0.6f + 0.4f * pulseAlpha)
                isDarkTheme -> boardBgStyle.borderDark
                else -> boardBgStyle.borderLight
            }

            drawRoundRect(
                color = matBgColor,
                topLeft = Offset(originX - 13.dp.toPx(), originY - 13.dp.toPx()),
                size = Size(gridW + 26.dp.toPx(), gridH + 26.dp.toPx()),
                cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
            )

            drawBackgroundTextureAsset(
                style = boardBgStyle,
                originX = originX,
                originY = originY,
                gridW = gridW,
                gridH = gridH,
                cellSize = cellSize,
                cols = gridCols,
                rows = gridRows,
                isDarkTheme = isDarkTheme
            )

            // Stitched perimeter border (pulses when Combo Fever >= 3x is active!)
            drawRoundRect(
                color = matBorderColor,
                topLeft = Offset(originX - 9.dp.toPx(), originY - 9.dp.toPx()),
                size = Size(gridW + 18.dp.toPx(), gridH + 18.dp.toPx()),
                cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx()),
                style = Stroke(
                    width = if (isSpatulaModeActive || isComboFever) 3.2.dp.toPx() else 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 7f), 0f)
                )
            )

            // Cozy Bookmark Ribbon Corner Asset
            val ribbonX = originX + gridW - 20.dp.toPx()
            val ribbonTop = originY - 13.dp.toPx()
            val ribbonPath = Path().apply {
                moveTo(ribbonX, ribbonTop)
                lineTo(ribbonX + 12.dp.toPx(), ribbonTop)
                lineTo(ribbonX + 12.dp.toPx(), ribbonTop + 18.dp.toPx())
                lineTo(ribbonX + 6.dp.toPx(), ribbonTop + 13.dp.toPx())
                lineTo(ribbonX, ribbonTop + 18.dp.toPx())
                close()
            }
            drawPath(ribbonPath, color = Color(0xFFE53935).copy(alpha = 0.85f))

            // 2. Draw Grid Pin Dots & Revealed Crochet Hamburger Stitches
            val dotColor = if (isDarkTheme) Color(0xFF685348) else Color(0xFFCBB29C)
            val subtleDotColor = if (isDarkTheme) Color(0xFF3E3029) else Color(0xFFE6D7C8)

            for (r in 0 until gridRows) {
                for (c in 0 until gridCols) {
                    val cell = GridCell(c, r)
                    val cx = originX + (c + 0.5f) * cellSize
                    val cy = originY + (r + 0.5f) * cellSize
                    val inPattern = stitchPattern.containsKey(cell)

                    if (cell in clearedCells) {
                        val yarnColor = stitchPattern[cell] ?: YarnArrowColor.SESAME_GOLD
                        drawCrochetStitchKnot(
                            center = Offset(cx, cy),
                            radius = cellSize * 0.22f,
                            yarnColor = yarnColor
                        )
                    } else {
                        drawCircle(
                            color = if (inPattern) dotColor else subtleDotColor,
                            radius = if (inPattern) cellSize * 0.075f else cellSize * 0.05f,
                            center = Offset(cx, cy)
                        )
                    }

                    if (cell in bossHazardCells) {
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.25f * pulseAlpha),
                            radius = cellSize * 0.36f,
                            center = Offset(cx, cy)
                        )
                        drawCircle(
                            color = Color(0xFF8B5CF6).copy(alpha = 0.7f),
                            radius = cellSize * 0.36f,
                            center = Offset(cx, cy),
                            style = Stroke(
                                width = 1.6.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                            )
                        )
                    }
                }
            }

            // 3. Optional Exit Ray Guide Dots
            if (showRayGuide) {
                for (arrow in arrows) {
                    val head = arrow.headCell
                    var cursor = head.step(arrow.headDirection)
                    while (cursor.col in 0 until gridCols && cursor.row in 0 until gridRows) {
                        val cx = originX + (cursor.col + 0.5f) * cellSize
                        val cy = originY + (cursor.row + 0.5f) * cellSize
                        drawCircle(
                            color = arrow.yarnColor.primaryColor.copy(alpha = 0.26f),
                            radius = cellSize * 0.05f,
                            center = Offset(cx, cy)
                        )
                        cursor = cursor.step(arrow.headDirection)
                    }
                }
            }

            // 4. Draw Launch Spot Explosions (Shockwave Rings, Starburst Rays & Confetti Particles!)
            for (explosion in launchExplosions) {
                val elapsed = (nowMs - explosion.startTimeMs).coerceIn(0L, 680L)
                val progress = elapsed / 680f
                drawLaunchSpotExplosion(
                    explosion = explosion,
                    originX = originX,
                    originY = originY,
                    cellSize = cellSize,
                    progress = progress
                )
            }

            // 5. Draw Narrow Minimalist Arrows
            for (arrow in arrows) {
                val isHinted = arrow.id == hintedArrowId
                val isXrayClear = arrow.id in xrayArrowIds
                val isBlocked = blockedAnim?.arrowId == arrow.id
                val bumpElapsed = if (isBlocked && blockedAnim != null) {
                    (nowMs - blockedAnim.timestampMs).coerceIn(0L, 600L) / 600f
                } else {
                    0f
                }

                val bumpFactor = if (isBlocked) {
                    sin(bumpElapsed * Math.PI.toFloat() * 3f) * (1f - bumpElapsed) * cellSize * 0.22f
                } else {
                    0f
                }
                val offsetPx = Offset(
                    x = arrow.headDirection.dx * bumpFactor,
                    y = arrow.headDirection.dy * bumpFactor
                )

                drawNarrowPuzzleArrow(
                    arrow = arrow,
                    originX = originX + offsetPx.x,
                    originY = originY + offsetPx.y,
                    cellSize = cellSize,
                    isHinted = isHinted,
                    isXrayClear = isXrayClear,
                    isBlocked = isBlocked,
                    pulseAlpha = pulseAlpha,
                    showYarnStitchRibs = showYarnStitchRibs,
                    arrowPinStyle = arrowPinStyle
                )
            }

            // 6. Collision Ring if an arrow was blocked
            if (blockedAnim != null) {
                val bx = originX + (blockedAnim.blockingCell.col + 0.5f) * cellSize
                val by = originY + (blockedAnim.blockingCell.row + 0.5f) * cellSize
                drawCircle(
                    color = Color(0xFFEF4444).copy(alpha = 0.5f),
                    radius = cellSize * 0.38f,
                    center = Offset(bx, by),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            // 7. Exiting Arrows sliding off with Blazing Afterburner Sparkle Trails!
            for (exiting in exitingArrows) {
                val progress = ((nowMs - exiting.startTimeMs).coerceIn(0L, 450L)) / 450f
                val slideDistance = progress * progress * (maxOf(gridCols, gridRows) + 2) * cellSize
                val slideOffset = Offset(
                    x = exiting.arrow.headDirection.dx * slideDistance,
                    y = exiting.arrow.headDirection.dy * slideDistance
                )

                // Draw blazing afterburner trail behind the flying arrow tail!
                val tailCell = exiting.arrow.tailCell
                val tailStartX = originX + (tailCell.col + 0.5f) * cellSize
                val tailStartY = originY + (tailCell.row + 0.5f) * cellSize
                val tailCurrentX = tailStartX + slideOffset.x
                val tailCurrentY = tailStartY + slideOffset.y

                drawLine(
                    color = exiting.arrow.yarnColor.highlightColor.copy(alpha = (1f - progress) * 0.7f),
                    start = Offset(tailStartX, tailStartY),
                    end = Offset(tailCurrentX, tailCurrentY),
                    strokeWidth = cellSize * 0.14f,
                    cap = StrokeCap.Round
                )

                // Trailing sparkle puffs along the flight path
                for (sparkIdx in 1..4) {
                    val frac = sparkIdx / 5f
                    val sx = tailStartX + slideOffset.x * frac
                    val sy = tailStartY + slideOffset.y * frac
                    drawCircle(
                        color = Color(0xFFFACC15).copy(alpha = (1f - progress) * 0.85f),
                        radius = cellSize * 0.08f * (1f - progress * 0.5f),
                        center = Offset(sx, sy)
                    )
                }

                drawNarrowPuzzleArrow(
                    arrow = exiting.arrow,
                    originX = originX + slideOffset.x,
                    originY = originY + slideOffset.y,
                    cellSize = cellSize,
                    isHinted = false,
                    isXrayClear = false,
                    isBlocked = false,
                    pulseAlpha = 1f,
                    showYarnStitchRibs = showYarnStitchRibs,
                    arrowPinStyle = arrowPinStyle
                )
            }
        }
    }
}

/**
 * Draws radial confetti particles, sesame-seed sparks, and expanding shockwave rings
 * at every cell the arrow just launched from!
 */
private fun DrawScope.drawLaunchSpotExplosion(
    explosion: LaunchExplosionAnim,
    originX: Float,
    originY: Float,
    cellSize: Float,
    progress: Float
) {
    val fade = (1f - progress).coerceIn(0f, 1f)
    val ringScale = 0.25f + progress * 1.15f

    for ((idx, cell) in explosion.cells.withIndex()) {
        val cx = originX + (cell.col + 0.5f) * cellSize
        val cy = originY + (cell.row + 0.5f) * cellSize

        // 1. Expanding Shockwave Ring at the spot
        drawCircle(
            color = explosion.primaryColor.copy(alpha = fade * 0.65f),
            radius = cellSize * 0.45f * ringScale,
            center = Offset(cx, cy),
            style = Stroke(width = (3.5f * fade).dp.toPx())
        )

        // 2. Inner Flash Core
        if (progress < 0.35f) {
            drawCircle(
                color = Color(0xFFFFFBEB).copy(alpha = (1f - progress / 0.35f) * 0.8f),
                radius = cellSize * 0.32f,
                center = Offset(cx, cy)
            )
        }

        // 3. 8 Radial Bursting Confetti & Yarn Sparks per cell
        val numSparks = 8
        for (s in 0 until numSparks) {
            val angle = (s * (Math.PI * 2.0 / numSparks)) + (idx * 0.4)
            val dist = cellSize * (0.15f + progress * 0.78f)
            val px = cx + cos(angle).toFloat() * dist
            val py = cy + sin(angle).toFloat() * dist
            val sparkColor = when (s % 3) {
                0 -> explosion.primaryColor
                1 -> Color(0xFFFACC15) // Golden sesame spark
                else -> explosion.highlightColor
            }
            drawCircle(
                color = sparkColor.copy(alpha = fade),
                radius = cellSize * 0.085f * fade,
                center = Offset(px, py)
            )
        }
    }

    // Extra multi-ring Combo Starburst at the head launch cell when combo >= 2!
    if (explosion.comboCount >= 2) {
        val hx = originX + (explosion.headCell.col + 0.5f) * cellSize
        val hy = originY + (explosion.headCell.row + 0.5f) * cellSize
        val burstRadius = cellSize * (0.35f + progress * 1.1f)
        for (ray in 0 until 12) {
            val a = ray * (Math.PI / 6.0)
            val innerR = burstRadius * 0.45f
            val outerR = burstRadius
            drawLine(
                color = Color(0xFFF59E0B).copy(alpha = fade * 0.85f),
                start = Offset(hx + cos(a).toFloat() * innerR, hy + sin(a).toFloat() * innerR),
                end = Offset(hx + cos(a).toFloat() * outerR, hy + sin(a).toFloat() * outerR),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawBackgroundTextureAsset(
    style: BoardBackgroundStyle,
    originX: Float,
    originY: Float,
    gridW: Float,
    gridH: Float,
    cellSize: Float,
    cols: Int,
    rows: Int,
    isDarkTheme: Boolean
) {
    val lineColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.04f)
    } else {
        Color(0xFF8D5524).copy(alpha = 0.06f)
    }
    when (style) {
        BoardBackgroundStyle.WAFFLE_LINEN -> {
            for (c in 0..cols) {
                val x = originX + c * cellSize
                drawLine(
                    color = lineColor,
                    start = Offset(x, originY),
                    end = Offset(x, originY + gridH),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (r in 0..rows) {
                val y = originY + r * cellSize
                drawLine(
                    color = lineColor,
                    start = Offset(originX, y),
                    end = Offset(originX + gridW, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        BoardBackgroundStyle.SESAME_KRAFT -> {
            val seedColor = if (isDarkTheme) {
                Color(0xFFFDE68A).copy(alpha = 0.07f)
            } else {
                Color(0xFFD97706).copy(alpha = 0.10f)
            }
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if ((r + c) % 2 == 0) {
                        val sx = originX + c * cellSize + cellSize * 0.16f
                        val sy = originY + r * cellSize + cellSize * 0.16f
                        drawOval(
                            color = seedColor,
                            topLeft = Offset(sx, sy),
                            size = Size(cellSize * 0.12f, cellSize * 0.06f)
                        )
                    }
                }
            }
        }
        BoardBackgroundStyle.MINT_BAKERY_CLOTH -> {
            val checkColor = if (isDarkTheme) {
                Color(0xFF34D399).copy(alpha = 0.05f)
            } else {
                Color(0xFF10B981).copy(alpha = 0.06f)
            }
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if ((r + c) % 2 == 1) {
                        drawRoundRect(
                            color = checkColor,
                            topLeft = Offset(originX + c * cellSize + 2f, originY + r * cellSize + 2f),
                            size = Size(cellSize - 4f, cellSize - 4f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawCrochetStitchKnot(
    center: Offset,
    radius: Float,
    yarnColor: YarnArrowColor
) {
    drawCircle(
        color = yarnColor.primaryColor.copy(alpha = 0.25f),
        radius = radius * 1.1f,
        center = center
    )
    val stroke = radius * 0.38f
    val d = radius * 0.52f
    drawLine(
        color = yarnColor.primaryColor.copy(alpha = 0.85f),
        start = Offset(center.x - d, center.y - d),
        end = Offset(center.x + d, center.y + d),
        strokeWidth = stroke,
        cap = StrokeCap.Round
    )
    drawLine(
        color = yarnColor.highlightColor.copy(alpha = 0.9f),
        start = Offset(center.x - d, center.y + d),
        end = Offset(center.x + d, center.y - d),
        strokeWidth = stroke * 0.85f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawNarrowPuzzleArrow(
    arrow: PuzzleArrow,
    originX: Float,
    originY: Float,
    cellSize: Float,
    isHinted: Boolean,
    isXrayClear: Boolean,
    isBlocked: Boolean,
    pulseAlpha: Float,
    showYarnStitchRibs: Boolean,
    arrowPinStyle: ArrowPinStyle
) {
    val shaftWidth = cellSize * 0.21f
    val headCenter = Offset(
        x = originX + (arrow.headCell.col + 0.5f) * cellSize,
        y = originY + (arrow.headCell.row + 0.5f) * cellSize
    )
    val tailCenter = Offset(
        x = originX + (arrow.tailCell.col + 0.5f) * cellSize,
        y = originY + (arrow.tailCell.row + 0.5f) * cellSize
    )
    val dir = arrow.headDirection

    val bodyColor = when {
        isBlocked -> Color(0xFFDC2626)
        else -> arrow.yarnColor.primaryColor
    }
    val shadowColor = when {
        isBlocked -> Color(0xFF7F1D1D)
        else -> arrow.yarnColor.deepShadowColor
    }
    val highlightColor = arrow.yarnColor.highlightColor

    val shaftPath = Path()
    val shadowPath = Path()
    val shadowOffset = Offset(0f, cellSize * 0.04f)

    if (arrow.cells.size == 1) {
        val tailStub = Offset(
            x = headCenter.x - dir.dx * (cellSize * 0.26f),
            y = headCenter.y - dir.dy * (cellSize * 0.26f)
        )
        val neckEnd = Offset(
            x = headCenter.x + dir.dx * (cellSize * 0.06f),
            y = headCenter.y + dir.dy * (cellSize * 0.06f)
        )
        shaftPath.moveTo(tailStub.x, tailStub.y)
        shaftPath.lineTo(neckEnd.x, neckEnd.y)

        shadowPath.moveTo(tailStub.x + shadowOffset.x, tailStub.y + shadowOffset.y)
        shadowPath.lineTo(neckEnd.x + shadowOffset.x, neckEnd.y + shadowOffset.y)
    } else {
        arrow.cells.forEachIndexed { index, cell ->
            val cx = originX + (cell.col + 0.5f) * cellSize
            val cy = originY + (cell.row + 0.5f) * cellSize
            if (index == 0) {
                shaftPath.moveTo(cx, cy)
                shadowPath.moveTo(cx + shadowOffset.x, cy + shadowOffset.y)
            } else {
                shaftPath.lineTo(cx, cy)
                shadowPath.lineTo(cx + shadowOffset.x, cy + shadowOffset.y)
            }
        }
    }

    if (isHinted || isXrayClear) {
        val haloColor = if (isHinted) Color(0xFFFACC15) else Color(0xFF10B981)
        drawPath(
            path = shaftPath,
            color = haloColor.copy(alpha = pulseAlpha * 0.85f),
            style = Stroke(
                width = shaftWidth * 1.95f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    drawPath(
        path = shadowPath,
        color = shadowColor.copy(alpha = 0.45f),
        style = Stroke(
            width = shaftWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    drawPath(
        path = shaftPath,
        color = bodyColor,
        style = Stroke(
            width = shaftWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    drawPath(
        path = shaftPath,
        color = highlightColor.copy(alpha = 0.55f),
        style = Stroke(
            width = shaftWidth * 0.32f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    if (showYarnStitchRibs && arrow.cells.size > 1) {
        for (i in 0 until arrow.cells.size - 1) {
            val c1 = arrow.cells[i]
            val c2 = arrow.cells[i + 1]
            val midX = originX + ((c1.col + c2.col) * 0.5f + 0.5f) * cellSize
            val midY = originY + ((c1.row + c2.row) * 0.5f + 0.5f) * cellSize
            val isHoriz = c1.row == c2.row
            val ribHalf = shaftWidth * 0.38f
            drawLine(
                color = shadowColor.copy(alpha = 0.35f),
                start = if (isHoriz) Offset(midX, midY - ribHalf) else Offset(midX - ribHalf, midY),
                end = if (isHoriz) Offset(midX, midY + ribHalf) else Offset(midX + ribHalf, midY),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    val pinPos = if (arrow.cells.size == 1) {
        Offset(
            x = headCenter.x - dir.dx * (cellSize * 0.25f),
            y = headCenter.y - dir.dy * (cellSize * 0.25f)
        )
    } else {
        tailCenter
    }
    when (arrowPinStyle) {
        ArrowPinStyle.MINIMAL_NARROW -> {
            drawCircle(
                color = highlightColor,
                radius = shaftWidth * 0.28f,
                center = pinPos
            )
        }
        ArrowPinStyle.CROCHET_HOOK_PIN -> {
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = shaftWidth * 0.62f,
                center = pinPos
            )
            drawCircle(
                color = Color(0xFFFFFBEB),
                radius = shaftWidth * 0.26f,
                center = pinPos
            )
        }
        ArrowPinStyle.BURGER_SKEWER_PIN -> {
            drawOval(
                color = Color(0xFFD97706),
                topLeft = Offset(pinPos.x - shaftWidth * 0.68f, pinPos.y - shaftWidth * 0.48f),
                size = Size(shaftWidth * 1.36f, shaftWidth * 0.96f)
            )
            drawCircle(
                color = Color(0xFFFEF3C7),
                radius = shaftWidth * 0.20f,
                center = pinPos
            )
        }
    }

    val tipDistance = cellSize * 0.34f
    val backDistance = cellSize * 0.05f
    val wingHalfWidth = cellSize * 0.24f

    val perpX = -dir.dy.toFloat()
    val perpY = dir.dx.toFloat()

    val tip = Offset(
        x = headCenter.x + dir.dx * tipDistance,
        y = headCenter.y + dir.dy * tipDistance
    )
    val wingLeft = Offset(
        x = headCenter.x - dir.dx * backDistance + perpX * wingHalfWidth,
        y = headCenter.y - dir.dy * backDistance + perpY * wingHalfWidth
    )
    val wingRight = Offset(
        x = headCenter.x - dir.dx * backDistance - perpX * wingHalfWidth,
        y = headCenter.y - dir.dy * backDistance - perpY * wingHalfWidth
    )

    val headShadowPath = Path().apply {
        moveTo(tip.x + shadowOffset.x, tip.y + shadowOffset.y)
        lineTo(wingLeft.x + shadowOffset.x, wingLeft.y + shadowOffset.y)
        lineTo(wingRight.x + shadowOffset.x, wingRight.y + shadowOffset.y)
        close()
    }
    drawPath(path = headShadowPath, color = shadowColor.copy(alpha = 0.45f))

    val headPath = Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(wingLeft.x, wingLeft.y)
        lineTo(wingRight.x, wingRight.y)
        close()
    }
    if (isHinted || isXrayClear) {
        val haloColor = if (isHinted) Color(0xFFFACC15) else Color(0xFF10B981)
        drawPath(
            path = headPath,
            color = haloColor.copy(alpha = pulseAlpha),
            style = Stroke(width = cellSize * 0.12f, join = StrokeJoin.Round, cap = StrokeCap.Round)
        )
    }
    drawPath(path = headPath, color = bodyColor)
    drawPath(
        path = headPath,
        color = bodyColor,
        style = Stroke(width = cellSize * 0.05f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    )

    val chevTip = Offset(
        x = headCenter.x + dir.dx * (cellSize * 0.20f),
        y = headCenter.y + dir.dy * (cellSize * 0.20f)
    )
    val chevLeft = Offset(
        x = headCenter.x + dir.dx * (cellSize * 0.02f) + perpX * (cellSize * 0.11f),
        y = headCenter.y + dir.dy * (cellSize * 0.02f) + perpY * (cellSize * 0.11f)
    )
    val chevRight = Offset(
        x = headCenter.x + dir.dx * (cellSize * 0.02f) - perpX * (cellSize * 0.11f),
        y = headCenter.y + dir.dy * (cellSize * 0.02f) - perpY * (cellSize * 0.11f)
    )
    val chevronPath = Path().apply {
        moveTo(chevLeft.x, chevLeft.y)
        lineTo(chevTip.x, chevTip.y)
        lineTo(chevRight.x, chevRight.y)
    }
    drawPath(
        path = chevronPath,
        color = Color.White.copy(alpha = 0.92f),
        style = Stroke(
            width = cellSize * 0.048f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}
