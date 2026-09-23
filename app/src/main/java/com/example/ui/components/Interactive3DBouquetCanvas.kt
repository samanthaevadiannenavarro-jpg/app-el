package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.*

data class Point3D(val x: Float, val y: Float, val z: Float)

@Composable
fun Interactive3DBouquetCanvas(
    flowerType: String,
    flowerColorName: String,
    flowerColor: Color,
    stemCount: Int,
    wrappingName: String,
    wrappingColor: Color,
    ribbonName: String,
    ribbonColor: Color,
    modifier: Modifier = Modifier
) {
    var yaw by remember { mutableFloatStateOf(20f) }
    var pitch by remember { mutableFloatStateOf(15f) }
    var zoom by remember { mutableFloatStateOf(1.05f) }
    var isAutoSpinning by remember { mutableStateOf(true) }

    // Auto-spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val animatedYaw by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinAngle"
    )

    val currentYaw = if (isAutoSpinning) (yaw + animatedYaw) % 360f else yaw

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF7F9),
                        Color(0xFFFFEEF3),
                        Color(0xFFFBE4EB)
                    )
                )
            )
            .pointerInput(isAutoSpinning) {
                detectDragGestures(
                    onDragStart = { isAutoSpinning = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        yaw = (yaw + dragAmount.x * 0.5f) % 360f
                        pitch = (pitch - dragAmount.y * 0.4f).coerceIn(-40f, 45f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("bouquet_3d_canvas")
        ) {
            val center = Offset(size.width / 2f, size.height * 0.54f)
            val scale = (size.minDimension * 0.38f) * zoom

            render3DBouquet(
                center = center,
                scale = scale,
                yaw = currentYaw,
                pitch = pitch,
                flowerType = flowerType,
                flowerColor = flowerColor,
                stemCount = stemCount,
                wrappingName = wrappingName,
                wrappingColor = wrappingColor,
                ribbonColor = ribbonColor
            )
        }

        // Overlay 3D Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(
                onClick = { isAutoSpinning = !isAutoSpinning },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("toggle_spin_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.85f)
                )
            ) {
                Icon(
                    imageVector = if (isAutoSpinning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isAutoSpinning) "Pause Rotation" else "Auto Spin",
                    tint = ElayaRoseDark,
                    modifier = Modifier.size(18.dp)
                )
            }

            FilledTonalIconButton(
                onClick = {
                    yaw = 20f
                    pitch = 15f
                    zoom = 1.05f
                    isAutoSpinning = false
                },
                modifier = Modifier
                    .size(36.dp)
                    .testTag("reset_view_button"),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.85f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Angle",
                    tint = ElayaRoseDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Top-left 3D Badge
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(StatusSuccess, CircleShape)
                )
                Text(
                    text = "3D Studio • Drag to rotate",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElayaTextPrimary
                )
            }
        }
    }
}

private fun DrawScope.render3DBouquet(
    center: Offset,
    scale: Float,
    yaw: Float,
    pitch: Float,
    flowerType: String,
    flowerColor: Color,
    stemCount: Int,
    wrappingName: String,
    wrappingColor: Color,
    ribbonColor: Color
) {
    val radYaw = Math.toRadians(yaw.toDouble()).toFloat()
    val radPitch = Math.toRadians(pitch.toDouble()).toFloat()

    // 3D rotation projection function
    fun project(p: Point3D): Offset {
        // Rotate around Y axis (Yaw)
        val cosY = cos(radYaw)
        val sinY = sin(radYaw)
        val x1 = p.x * cosY + p.z * sinY
        val z1 = -p.x * sinY + p.z * cosY

        // Rotate around X axis (Pitch)
        val cosX = cos(radPitch)
        val sinX = sin(radPitch)
        val y2 = p.y * cosX - z1 * sinX
        val z2 = p.y * sinX + z1 * cosX

        // Perspective depth
        val cameraDistance = 3.5f
        val factor = cameraDistance / (cameraDistance + z2)
        val screenX = center.x + x1 * scale * factor
        val screenY = center.y + y2 * scale * factor
        return Offset(screenX, screenY)
    }

    // 1. Stems (Bottom)
    val stemBottom = project(Point3D(0f, 1.45f, 0f))
    val stemLeft = project(Point3D(-0.18f, 1.35f, 0.05f))
    val stemRight = project(Point3D(0.18f, 1.35f, -0.05f))
    val stemTop = project(Point3D(0f, 0.65f, 0f))

    drawLine(
        color = Color(0xFF2E6334),
        start = stemTop,
        end = stemBottom,
        strokeWidth = 10f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF38763E),
        start = stemTop,
        end = stemLeft,
        strokeWidth = 7f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF2A5930),
        start = stemTop,
        end = stemRight,
        strokeWidth = 7f,
        cap = StrokeCap.Round
    )

    // 2. Back Wrapping Cone
    val wrapBottom = project(Point3D(0f, 0.7f, 0f))
    val wrapBackLeft = project(Point3D(-0.95f, -0.3f, -0.5f))
    val wrapBackTop = project(Point3D(0f, -0.85f, -0.6f))
    val wrapBackRight = project(Point3D(0.95f, -0.3f, -0.5f))

    val backWrapPath = Path().apply {
        moveTo(wrapBottom.x, wrapBottom.y)
        lineTo(wrapBackLeft.x, wrapBackLeft.y)
        cubicTo(
            wrapBackLeft.x, wrapBackTop.y,
            wrapBackRight.x, wrapBackTop.y,
            wrapBackRight.x, wrapBackRight.y
        )
        close()
    }
    drawPath(
        path = backWrapPath,
        brush = Brush.linearGradient(
            colors = listOf(
                wrappingColor.copy(alpha = 0.85f),
                wrappingColor.copy(red = (wrappingColor.red * 0.7f).coerceIn(0f, 1f), alpha = 0.95f)
            )
        )
    )

    // 3. Flower nodes generation in hemispherical dome
    val flowerNodes = mutableListOf<Triple<Point3D, Float, Int>>() // position, sizeMultiplier, index
    val count = stemCount.coerceIn(6, 36)
    val goldenRatio = (1.0 + sqrt(5.0)) / 2.0

    for (i in 0 until count) {
        val yDome = -0.1f - (i.toFloat() / count.toFloat()) * 0.65f
        val radiusAtY = sqrt((1f - (-yDome * 0.8f).pow(2)).coerceAtLeast(0.04f)) * 0.78f
        val theta = (i * 2.0 * Math.PI / goldenRatio).toFloat()
        val x = cos(theta) * radiusAtY
        val z = sin(theta) * radiusAtY
        flowerNodes.add(Triple(Point3D(x, yDome, z), 1.0f - (i.toFloat() / count.toFloat()) * 0.2f, i))
    }

    // Sort flowers by rotated Z-depth so far ones are drawn first!
    val cosY = cos(radYaw)
    val sinY = sin(radYaw)
    val cosX = cos(radPitch)
    val sinX = sin(radPitch)

    val sortedFlowers = flowerNodes.map { (p, sz, idx) ->
        val x1 = p.x * cosY + p.z * sinY
        val z1 = -p.x * sinY + p.z * cosY
        val y2 = p.y * cosX - z1 * sinX
        val z2 = p.y * sinX + z1 * cosX
        Quad(p, sz, idx, z2)
    }.sortedBy { it.rotatedZ }

    // Draw Back & Mid flowers (z2 <= 0.1f)
    for (f in sortedFlowers) {
        if (f.rotatedZ <= 0.1f) {
            val proj = project(f.point)
            drawSingleFlower(
                flowerType = flowerType,
                flowerColor = flowerColor,
                pos = proj,
                flowerRadius = 26f * f.sizeMultiplier * (scale / 120f),
                depthAlpha = (0.7f + (f.rotatedZ + 0.5f) * 0.3f).coerceIn(0.6f, 1f)
            )
        }
    }

    // 4. Front Wrapping Paper (Cone with folded collar lapels)
    val wrapFrontLeft = project(Point3D(-0.75f, 0.05f, 0.45f))
    val wrapFrontCenter = project(Point3D(0f, 0.25f, 0.55f))
    val wrapFrontRight = project(Point3D(0.75f, 0.05f, 0.45f))

    val frontWrapPath = Path().apply {
        moveTo(wrapBottom.x, wrapBottom.y)
        lineTo(wrapFrontLeft.x, wrapFrontLeft.y)
        cubicTo(
            wrapFrontLeft.x + 20f, wrapFrontCenter.y,
            wrapFrontRight.x - 20f, wrapFrontCenter.y,
            wrapFrontRight.x, wrapFrontRight.y
        )
        close()
    }
    drawPath(
        path = frontWrapPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                wrappingColor,
                wrappingColor.copy(alpha = 0.95f),
                wrappingColor.copy(red = (wrappingColor.red * 0.85f).coerceIn(0f, 1f))
            )
        )
    )

    // Wrapping fold details
    drawPath(
        path = frontWrapPath,
        color = Color.White.copy(alpha = 0.35f),
        style = Stroke(width = 2.5f)
    )

    // Draw Front flowers (z2 > 0.1f)
    for (f in sortedFlowers) {
        if (f.rotatedZ > 0.1f) {
            val proj = project(f.point)
            drawSingleFlower(
                flowerType = flowerType,
                flowerColor = flowerColor,
                pos = proj,
                flowerRadius = 29f * f.sizeMultiplier * (scale / 120f),
                depthAlpha = 1.0f
            )
        }
    }

    // 5. Ribbon Bow & Tied Knot
    val bowCenter = project(Point3D(0f, 0.68f, 0.45f))
    val bowLeftLoop = project(Point3D(-0.35f, 0.62f, 0.5f))
    val bowRightLoop = project(Point3D(0.35f, 0.62f, 0.5f))
    val ribbonTailLeft = project(Point3D(-0.25f, 1.15f, 0.4f))
    val ribbonTailRight = project(Point3D(0.25f, 1.18f, 0.4f))

    // Ribbon tails
    val leftTailPath = Path().apply {
        moveTo(bowCenter.x, bowCenter.y)
        quadraticTo(bowCenter.x - 20f, bowCenter.y + 40f, ribbonTailLeft.x, ribbonTailLeft.y)
    }
    val rightTailPath = Path().apply {
        moveTo(bowCenter.x, bowCenter.y)
        quadraticTo(bowCenter.x + 20f, bowCenter.y + 40f, ribbonTailRight.x, ribbonTailRight.y)
    }
    drawPath(leftTailPath, color = ribbonColor, style = Stroke(width = 12f, cap = StrokeCap.Round))
    drawPath(rightTailPath, color = ribbonColor, style = Stroke(width = 12f, cap = StrokeCap.Round))

    // Left bow loop
    drawOval(
        color = ribbonColor,
        topLeft = Offset(bowLeftLoop.x - 18f, bowLeftLoop.y - 12f),
        size = Size(36f, 24f)
    )
    // Right bow loop
    drawOval(
        color = ribbonColor,
        topLeft = Offset(bowRightLoop.x - 18f, bowRightLoop.y - 12f),
        size = Size(36f, 24f)
    )
    // Center knot
    drawCircle(
        color = ribbonColor.copy(red = (ribbonColor.red * 0.85f).coerceIn(0f, 1f)),
        radius = 11f,
        center = bowCenter
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.35f),
        radius = 6f,
        center = Offset(bowCenter.x - 3f, bowCenter.y - 3f)
    )
}

private data class Quad<T, U, V, W>(val point: T, val sizeMultiplier: U, val index: V, val rotatedZ: W)

private fun DrawScope.drawSingleFlower(
    flowerType: String,
    flowerColor: Color,
    pos: Offset,
    flowerRadius: Float,
    depthAlpha: Float
) {
    val r = flowerRadius.coerceAtLeast(10f)

    when {
        flowerType.contains("Sunflower", ignoreCase = true) -> {
            // Sunflower: golden radiating petals + dark disc florets
            val petalCount = 12
            for (p in 0 until petalCount) {
                val angle = (p * 2 * Math.PI / petalCount).toFloat()
                val px = pos.x + cos(angle) * (r * 0.9f)
                val py = pos.y + sin(angle) * (r * 0.9f)
                drawCircle(
                    color = Color(0xFFF59E0B).copy(alpha = depthAlpha),
                    radius = r * 0.38f,
                    center = Offset(px, py)
                )
            }
            // Brown seeded disc center
            drawCircle(
                color = Color(0xFF451A03).copy(alpha = depthAlpha),
                radius = r * 0.52f,
                center = pos
            )
            drawCircle(
                color = Color(0xFF78350F).copy(alpha = depthAlpha),
                radius = r * 0.32f,
                center = pos
            )
        }

        flowerType.contains("Tulip", ignoreCase = true) -> {
            // Tulip: cup shape with layered vertical curved petals
            drawOval(
                color = flowerColor.copy(red = (flowerColor.red * 0.8f).coerceIn(0f, 1f), alpha = depthAlpha),
                topLeft = Offset(pos.x - r * 0.65f, pos.y - r * 0.95f),
                size = Size(r * 1.3f, r * 1.8f)
            )
            drawOval(
                color = flowerColor.copy(alpha = depthAlpha),
                topLeft = Offset(pos.x - r * 0.5f, pos.y - r * 0.85f),
                size = Size(r * 1.0f, r * 1.6f)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.3f * depthAlpha),
                topLeft = Offset(pos.x - r * 0.3f, pos.y - r * 0.7f),
                size = Size(r * 0.4f, r * 0.9f)
            )
        }

        flowerType.contains("Carnation", ignoreCase = true) -> {
            // Carnation: ruffled dense petal bursts
            for (layer in 4 downTo 1) {
                val layerR = r * (layer / 4f)
                val points = 8
                for (pt in 0 until points) {
                    val angle = (pt * 2 * Math.PI / points + (layer * 0.4)).toFloat()
                    val cx = pos.x + cos(angle) * (layerR * 0.45f)
                    val cy = pos.y + sin(angle) * (layerR * 0.45f)
                    drawCircle(
                        color = flowerColor.copy(
                            red = (flowerColor.red * (0.7f + layer * 0.08f)).coerceIn(0f, 1f),
                            alpha = depthAlpha
                        ),
                        radius = layerR * 0.48f,
                        center = Offset(cx, cy)
                    )
                }
            }
        }

        else -> {
            // Default: Rose (spiral petal layers radiating with soft shading)
            // Outer petal base
            drawCircle(
                color = flowerColor.copy(red = (flowerColor.red * 0.75f).coerceIn(0f, 1f), alpha = depthAlpha),
                radius = r,
                center = pos
            )
            // Spiral mid petals
            for (i in 0 until 5) {
                val angle = (i * 2 * Math.PI / 5).toFloat()
                val px = pos.x + cos(angle) * (r * 0.45f)
                val py = pos.y + sin(angle) * (r * 0.45f)
                drawCircle(
                    color = flowerColor.copy(alpha = depthAlpha),
                    radius = r * 0.58f,
                    center = Offset(px, py)
                )
            }
            // Inner bud core
            drawCircle(
                color = flowerColor.copy(
                    red = (flowerColor.red * 0.85f).coerceIn(0f, 1f),
                    green = (flowerColor.green * 0.75f).coerceIn(0f, 1f),
                    alpha = depthAlpha
                ),
                radius = r * 0.35f,
                center = pos
            )
            // Center highlight swirl
            drawCircle(
                color = Color.White.copy(alpha = 0.4f * depthAlpha),
                radius = r * 0.15f,
                center = Offset(pos.x - r * 0.1f, pos.y - r * 0.1f)
            )
        }
    }
}
