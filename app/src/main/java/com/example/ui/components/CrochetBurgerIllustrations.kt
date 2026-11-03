package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BossType
import com.example.model.BurgerLayerType
import com.example.model.BurgerYarnLayer
import com.example.model.CrewMember

/**
 * Displays Sandra's real uploaded photo (from R.drawable.img_sandra_photo) inside a cozy
 * stitched crochet badge, or custom illustrated portraits for Naty and Ally!
 */
@Composable
fun CrewAvatarBadge(
    member: CrewMember,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    if (member == CrewMember.SANDRA) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(2.dp, member.badgeColor, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_sandra_photo),
                contentDescription = "Sandra Portrait Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val center = Offset(r, r)

        drawCircle(color = member.badgeColor.copy(alpha = 0.2f), radius = r, center = center)
        drawCircle(
            color = member.badgeColor,
            radius = r - 2.dp.toPx(),
            center = center,
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 5f), 0f)
            )
        )

        drawCircle(
            color = member.hairColor,
            radius = r * 0.56f,
            center = Offset(center.x, center.y - r * 0.04f)
        )

        drawRoundRect(
            color = member.shirtColor,
            topLeft = Offset(center.x - r * 0.48f, center.y + r * 0.32f),
            size = Size(r * 0.96f, r * 0.48f),
            cornerRadius = CornerRadius(r * 0.25f, r * 0.25f)
        )

        val skinTone = when (member) {
            CrewMember.SANDRA -> Color(0xFFF3C6A5)
            CrewMember.NATY -> Color(0xFFE8B48F)
            CrewMember.ALLY -> Color(0xFFF7D3BA)
        }
        drawCircle(
            color = skinTone,
            radius = r * 0.40f,
            center = Offset(center.x, center.y - r * 0.02f)
        )

        val eyeY = center.y - r * 0.05f
        drawCircle(Color(0xFF2B1B12), radius = r * 0.055f, center = Offset(center.x - r * 0.14f, eyeY))
        drawCircle(Color(0xFF2B1B12), radius = r * 0.055f, center = Offset(center.x + r * 0.14f, eyeY))

        when (member) {
            CrewMember.NATY -> {
                drawCircle(
                    color = Color(0xFFFFFBEB),
                    radius = r * 0.22f,
                    center = Offset(center.x, center.y - r * 0.52f)
                )
                drawCircle(
                    color = Color(0xFFFFFBEB),
                    radius = r * 0.17f,
                    center = Offset(center.x - r * 0.16f, center.y - r * 0.46f)
                )
                drawCircle(
                    color = Color(0xFFFFFBEB),
                    radius = r * 0.17f,
                    center = Offset(center.x + r * 0.16f, center.y - r * 0.46f)
                )
            }
            CrewMember.ALLY -> {
                drawCircle(
                    color = Color(0xFFF472B6),
                    radius = r * 0.13f,
                    center = Offset(center.x + r * 0.26f, center.y - r * 0.36f)
                )
                drawCircle(
                    color = Color(0xFFFCE7F3),
                    radius = r * 0.05f,
                    center = Offset(center.x + r * 0.26f, center.y - r * 0.36f)
                )
            }
            CrewMember.SANDRA -> Unit
        }

        drawLine(
            color = Color(0xFF8C2D19),
            start = Offset(center.x - r * 0.10f, center.y + r * 0.14f),
            end = Offset(center.x + r * 0.10f, center.y + r * 0.14f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Interactive Turn-Based Boss Arena Header shown during Boss Battles (Messy House & Broken AC!).
 * Every cleared arrow flies up to damage the Boss, and every 3 moves the Boss takes its turn!
 */
@Composable
fun TurnBasedBossArenaCard(
    bossType: BossType,
    currentHp: Int,
    maxHp: Int,
    movesLeftInTurn: Int,
    lastDamage: Int?,
    statusBanner: String?,
    modifier: Modifier = Modifier
) {
    val hpFraction = if (maxHp <= 0) 0f else (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = bossType.badgeColor.copy(alpha = 0.14f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, bossType.badgeColor.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Custom Boss Illustration Canvas (Messy House / Broken AC / Duo)
                BossArtworkCanvas(
                    bossType = bossType,
                    hpFraction = hpFraction,
                    modifier = Modifier.size(68.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bossType.bossName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = bossType.badgeColor
                        )
                        if (lastDamage != null && lastDamage > 0) {
                            Surface(
                                color = Color(0xFFEF4444),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "-$lastDamage HP!",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Boss HP Bar
                    LinearProgressIndicator(
                        progress = { hpFraction },
                        color = bossType.badgeColor,
                        trackColor = bossType.badgeColor.copy(alpha = 0.22f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Boss HP: $currentHp / $maxHp",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "Your Turn: $movesLeftInTurn Arrow${if (movesLeftInTurn == 1) "" else "s"} Left",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (!statusBanner.isNullOrBlank()) {
                Text(
                    text = statusBanner,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BossArtworkCanvas(
    bossType: BossType,
    hpFraction: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1F1916))
    ) {
        val w = size.width
        val h = size.height

        when (bossType) {
            BossType.MESSY_HOUSE -> drawMessyHouseBoss(w, h)
            BossType.BROKEN_AC -> drawBrokenAcBoss(w, h, hpFraction)
            BossType.LAUNDRY_YARN_TORNADO -> {
                drawMessyHouseBoss(w, h)
                // Extra tangled purple yarn swirl
                drawCircle(
                    color = Color(0xFFEC4899),
                    radius = w * 0.22f,
                    center = Offset(w * 0.72f, h * 0.34f),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            BossType.ULTIMATE_CHORE_DUO -> {
                drawMessyHouseBoss(w * 0.65f, h)
                drawBrokenAcBoss(w, h, hpFraction)
            }
        }
    }
}

private fun DrawScope.drawMessyHouseBoss(w: Float, h: Float) {
    // Crooked house walls & roof
    val roofPath = Path().apply {
        moveTo(w * 0.12f, h * 0.44f)
        lineTo(w * 0.50f, h * 0.14f)
        lineTo(w * 0.88f, h * 0.44f)
        close()
    }
    drawPath(roofPath, color = Color(0xFFB45309))
    drawRoundRect(
        color = Color(0xFFFDE68A),
        topLeft = Offset(w * 0.20f, h * 0.42f),
        size = Size(w * 0.60f, h * 0.46f),
        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
    )
    // Overflowing laundry & dish pile mounds at the door
    drawCircle(color = Color(0xFF8B5CF6), radius = w * 0.14f, center = Offset(w * 0.35f, h * 0.76f))
    drawCircle(color = Color(0xFFEC4899), radius = w * 0.12f, center = Offset(w * 0.55f, h * 0.78f))
    drawCircle(color = Color(0xFF94A3B8), radius = w * 0.11f, center = Offset(w * 0.70f, h * 0.75f))
    // Crooked window
    drawRect(
        color = Color(0xFF0EA5E9),
        topLeft = Offset(w * 0.36f, h * 0.50f),
        size = Size(w * 0.18f, h * 0.14f)
    )
}

private fun DrawScope.drawBrokenAcBoss(w: Float, h: Float, hpFraction: Float) {
    // Wall Air Conditioner Box
    drawRoundRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(w * 0.14f, h * 0.24f),
        size = Size(w * 0.72f, h * 0.44f),
        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
    )
    // AC Vents
    for (i in 0..2) {
        val vy = h * (0.34f + i * 0.09f)
        drawLine(
            color = Color(0xFF475569),
            start = Offset(w * 0.22f, vy),
            end = Offset(w * 0.62f, vy),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
    // Red overheating thermostat indicator (turns cool blue as HP drops!)
    val tempColor = if (hpFraction > 0.35f) Color(0xFFEF4444) else Color(0xFF38BDF8)
    drawCircle(
        color = tempColor,
        radius = w * 0.09f,
        center = Offset(w * 0.74f, h * 0.42f)
    )
    // Hot wavy steam / dripping condensation lines below AC
    drawLine(
        color = tempColor,
        start = Offset(w * 0.32f, h * 0.70f),
        end = Offset(w * 0.28f, h * 0.88f),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
    drawLine(
        color = tempColor,
        start = Offset(w * 0.54f, h * 0.70f),
        end = Offset(w * 0.58f, h * 0.88f),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}

/**
 * Custom Crochet-Hamburger Score & Combo Pill Display Asset.
 * Styled like a sesame brioche bun top with stitched border.
 */
@Composable
fun CrochetBurgerScoreBadge(
    score: Int,
    comboStreak: Int,
    hasShield: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFD97706), Color(0xFFB45309))
                )
            )
            .border(1.5.dp, Color(0xFFFEF3C7), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFFFFBEB),
            fontWeight = FontWeight.Bold
        )
        if (comboStreak >= 2) {
            Surface(
                color = Color(0xFF10B981),
                shape = CircleShape
            ) {
                Text(
                    text = "${comboStreak}x Combo",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        if (hasShield) {
            Surface(
                color = Color(0xFFEC4899),
                shape = CircleShape
            ) {
                Text(
                    text = "Brioche Shield",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Interactive Crochet Burger + Sweet Baking + Cozy Reading Stacker Canvas
 * used on the Burger Yarn & Cafe Kitchen screen.
 */
@Composable
fun CrochetBurgerStackCanvas(
    layers: List<BurgerYarnLayer>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val cx = size.width / 2f
        val plateY = size.height - 26.dp.toPx()
        val burgerWidth = (size.width * 0.56f).coerceAtMost(230.dp.toPx())

        drawOval(
            color = Color(0xFFE5C9AD),
            topLeft = Offset(cx - burgerWidth * 0.72f, plateY - 12.dp.toPx()),
            size = Size(burgerWidth * 1.44f, 26.dp.toPx())
        )
        drawOval(
            color = Color(0xFFFFFBF5),
            topLeft = Offset(cx - burgerWidth * 0.64f, plateY - 9.dp.toPx()),
            size = Size(burgerWidth * 1.28f, 20.dp.toPx())
        )

        var currentY = plateY - 14.dp.toPx()
        val stepH = ((size.height - 64.dp.toPx()) / (layers.size.coerceAtLeast(1) + 1))
            .coerceIn(14.dp.toPx(), 25.dp.toPx())

        layers.forEach { layer ->
            drawSingleCollectibleLayer(
                layer = layer,
                cx = cx,
                bottomY = currentY,
                width = burgerWidth
            )
            currentY -= stepH
        }
    }
}

private fun DrawScope.drawSingleCollectibleLayer(
    layer: BurgerYarnLayer,
    cx: Float,
    bottomY: Float,
    width: Float
) {
    val dashStroke = Stroke(
        width = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 6f), 0f)
    )
    when (layer.layerType) {
        BurgerLayerType.BOTTOM_BUN -> {
            val h = 24.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.48f, bottomY - h),
                size = Size(width * 0.96f, h),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
            drawRoundRect(
                color = layer.accentColor,
                topLeft = Offset(cx - width * 0.46f, bottomY - h + 3.dp.toPx()),
                size = Size(width * 0.92f, h - 6.dp.toPx()),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                style = dashStroke
            )
        }
        BurgerLayerType.PATTY -> {
            val h = 22.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.52f, bottomY - h),
                size = Size(width * 1.04f, h),
                cornerRadius = CornerRadius(11.dp.toPx(), 11.dp.toPx())
            )
            drawRoundRect(
                color = layer.accentColor,
                topLeft = Offset(cx - width * 0.49f, bottomY - h + 3.dp.toPx()),
                size = Size(width * 0.98f, h - 6.dp.toPx()),
                cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx()),
                style = dashStroke
            )
        }
        BurgerLayerType.CHEESE -> {
            val h = 14.dp.toPx()
            val path = Path().apply {
                moveTo(cx - width * 0.52f, bottomY - h)
                lineTo(cx + width * 0.52f, bottomY - h)
                lineTo(cx + width * 0.48f, bottomY)
                lineTo(cx + width * 0.18f, bottomY + 8.dp.toPx())
                lineTo(cx - width * 0.12f, bottomY - 2.dp.toPx())
                lineTo(cx - width * 0.32f, bottomY + 7.dp.toPx())
                close()
            }
            drawPath(path, color = layer.primaryColor)
        }
        BurgerLayerType.LETTUCE -> {
            val y = bottomY - 10.dp.toPx()
            for (i in -4..4) {
                drawCircle(
                    color = if (i % 2 == 0) layer.primaryColor else layer.accentColor,
                    radius = 11.dp.toPx(),
                    center = Offset(cx + i * (width * 0.11f), y)
                )
            }
        }
        BurgerLayerType.TOMATO -> {
            val h = 16.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.44f, bottomY - h),
                size = Size(width * 0.88f, h),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
            drawRoundRect(
                color = layer.accentColor,
                topLeft = Offset(cx - width * 0.40f, bottomY - h + 3.dp.toPx()),
                size = Size(width * 0.80f, h - 6.dp.toPx()),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = dashStroke
            )
        }
        BurgerLayerType.PICKLES -> {
            for (i in -1..1) {
                val px = cx + i * (width * 0.26f)
                drawOval(
                    color = layer.primaryColor,
                    topLeft = Offset(px - 18.dp.toPx(), bottomY - 14.dp.toPx()),
                    size = Size(36.dp.toPx(), 14.dp.toPx())
                )
            }
        }
        BurgerLayerType.SECRET_SAUCE -> {
            val h = 12.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.42f, bottomY - h),
                size = Size(width * 0.84f, h),
                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
        }
        BurgerLayerType.TOP_BUN -> {
            val h = 38.dp.toPx()
            val bunPath = Path().apply {
                moveTo(cx - width * 0.50f, bottomY)
                cubicTo(
                    cx - width * 0.50f, bottomY - h * 1.2f,
                    cx + width * 0.50f, bottomY - h * 1.2f,
                    cx + width * 0.50f, bottomY
                )
                close()
            }
            drawPath(bunPath, color = layer.primaryColor)
            drawPath(bunPath, color = layer.accentColor, style = dashStroke)
            for (i in -2..2) {
                drawOval(
                    color = layer.accentColor,
                    topLeft = Offset(cx + i * 18.dp.toPx() - 4.dp.toPx(), bottomY - h * 0.58f),
                    size = Size(8.dp.toPx(), 4.dp.toPx())
                )
            }
        }
        BurgerLayerType.BAKED_CROISSANT -> {
            val h = 18.dp.toPx()
            drawOval(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.36f, bottomY - h),
                size = Size(width * 0.72f, h)
            )
            drawOval(
                color = layer.accentColor,
                topLeft = Offset(cx - width * 0.32f, bottomY - h + 3.dp.toPx()),
                size = Size(width * 0.64f, h - 6.dp.toPx()),
                style = dashStroke
            )
        }
        BurgerLayerType.FROSTED_CUPCAKE -> {
            val h = 20.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.30f, bottomY - h),
                size = Size(width * 0.60f, h),
                cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFEF4444),
                radius = 5.dp.toPx(),
                center = Offset(cx, bottomY - h - 3.dp.toPx())
            )
        }
        BurgerLayerType.RECIPE_STORYBOOK -> {
            val h = 18.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.54f, bottomY - h),
                size = Size(width * 1.08f, h),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFFFFFBEB),
                topLeft = Offset(cx - width * 0.50f, bottomY - h + 4.dp.toPx()),
                size = Size(width * 1.00f, h - 8.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
            drawLine(
                color = Color(0xFFE53935),
                start = Offset(cx + width * 0.25f, bottomY - h + 4.dp.toPx()),
                end = Offset(cx + width * 0.30f, bottomY + 6.dp.toPx()),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        BurgerLayerType.BAKER_WHISK_BOOKMARK -> {
            val h = 15.dp.toPx()
            drawRoundRect(
                color = layer.primaryColor,
                topLeft = Offset(cx - width * 0.40f, bottomY - h),
                size = Size(width * 0.80f, h),
                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
            )
        }
    }
}

/**
 * Decorative Cozy Cafe Banner Canvas with Crochet Hamburger, Open Book, Baker's Cupcake,
 * and Arrow Puzzle accents.
 */
@Composable
fun SandraCafeHeroArt(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        drawRoundRect(
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0xFF9A3412),
                    Color(0xFFC2410C),
                    Color(0xFFB45309)
                )
            ),
            size = size,
            cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
        )

        drawRoundRect(
            color = Color(0xFFFDE68A).copy(alpha = 0.45f),
            topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
            size = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx()),
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f)
            )
        )

        val bookX = 42.dp.toPx()
        val bookY = size.height * 0.55f
        drawRoundRect(
            color = Color(0xFFFFFBEB),
            topLeft = Offset(bookX - 22.dp.toPx(), bookY - 14.dp.toPx()),
            size = Size(21.dp.toPx(), 28.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFFFEF3C7),
            topLeft = Offset(bookX + 1.dp.toPx(), bookY - 14.dp.toPx()),
            size = Size(21.dp.toPx(), 28.dp.toPx()),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        drawLine(
            color = Color(0xFFEF4444),
            start = Offset(bookX, bookY - 14.dp.toPx()),
            end = Offset(bookX + 4.dp.toPx(), bookY + 20.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        val rightX = size.width - 44.dp.toPx()
        val rightY = size.height * 0.54f
        drawCircle(
            color = Color(0xFFF59E0B),
            radius = 20.dp.toPx(),
            center = Offset(rightX, rightY - 2.dp.toPx())
        )
        drawLine(
            color = Color(0xFF34D399),
            start = Offset(rightX - 22.dp.toPx(), rightY + 2.dp.toPx()),
            end = Offset(rightX + 22.dp.toPx(), rightY + 2.dp.toPx()),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF5C2C16),
            start = Offset(rightX - 21.dp.toPx(), rightY + 8.dp.toPx()),
            end = Offset(rightX + 21.dp.toPx(), rightY + 8.dp.toPx()),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
