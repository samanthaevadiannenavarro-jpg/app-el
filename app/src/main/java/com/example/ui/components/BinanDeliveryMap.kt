package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BinanLocations
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun BinanDeliveryMap(
    originShopName: String,
    originBarangay: String,
    destCustomerName: String,
    destBarangay: String,
    orderStatus: String,
    riderName: String,
    etaMinutes: Int,
    modifier: Modifier = Modifier
) {
    val originLoc = BinanLocations.findBarangay(originBarangay)
    val destLoc = BinanLocations.findBarangay(destBarangay)

    // Animated rider transit progression (0.0 = origin, 1.0 = delivered)
    val infiniteTransition = rememberInfiniteTransition(label = "riderMovement")
    val simulatedProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "riderProgress"
    )

    val currentProgress = when (orderStatus) {
        "Completed" -> 1.0f
        "Out for Delivery" -> simulatedProgress
        "Ready for Delivery" -> 0.12f
        else -> 0.05f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFE8ECEF))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("binan_live_map_canvas")
        ) {
            renderBinanMap(
                originLoc = originLoc,
                destLoc = destLoc,
                progress = currentProgress,
                orderStatus = orderStatus
            )
        }

        // Top-left GPS Telemetry chip
        Surface(
            color = Color.White.copy(alpha = 0.92f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            if (orderStatus == "Out for Delivery") StatusSuccess else StatusInfo,
                            CircleShape
                        )
                )
                Text(
                    text = "GPS Live • Biñan, Laguna",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ElayaTextPrimary
                )
            }
        }

        // Bottom status badge overlay
        Surface(
            color = Color.White.copy(alpha = 0.95f),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .fillMaxWidth(),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElayaPinkLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = null,
                            tint = ElayaRoseDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (orderStatus == "Out for Delivery") "In Transit ($riderName)" else orderStatus,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = ElayaTextPrimary
                        )
                        Text(
                            text = "To: $destBarangay, Biñan (ETA ~${if (orderStatus == "Completed") "0" else etaMinutes} mins)",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElayaTextSecondary
                        )
                    }
                }

                Surface(
                    color = when (orderStatus) {
                        "Completed" -> StatusSuccess.copy(alpha = 0.15f)
                        "Out for Delivery" -> ElayaRoseDark.copy(alpha = 0.15f)
                        else -> StatusPending.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = orderStatus.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (orderStatus) {
                            "Completed" -> StatusSuccess
                            "Out for Delivery" -> ElayaRoseDark
                            else -> StatusPending
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.renderBinanMap(
    originLoc: com.example.data.model.BinanBarangay,
    destLoc: com.example.data.model.BinanBarangay,
    progress: Float,
    orderStatus: String
) {
    val w = size.width
    val h = size.height

    // 1. Water body: Laguna de Bay coastline on eastern side
    val waterPath = Path().apply {
        moveTo(w * 0.85f, 0f)
        cubicTo(w * 0.82f, h * 0.3f, w * 0.90f, h * 0.7f, w * 0.84f, h)
        lineTo(w, h)
        lineTo(w, 0f)
        close()
    }
    drawPath(waterPath, color = Color(0xFFC7E2F7))

    // 2. Road Network Representation in Biñan:
    // South Luzon Expressway (SLEX) & CALAX
    val slexPath = Path().apply {
        moveTo(w * 0.35f, 0f)
        lineTo(w * 0.42f, h)
    }
    drawLine(
        color = Color(0xFFFACC15),
        start = Offset(w * 0.35f, 0f),
        end = Offset(w * 0.42f, h),
        strokeWidth = 9f
    )
    drawLine(
        color = Color.White,
        start = Offset(w * 0.35f, 0f),
        end = Offset(w * 0.42f, h),
        strokeWidth = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
    )

    // Manila South Road (National Highway through San Vicente, Canlalay, Zapote)
    val natHighwayPath = Path().apply {
        moveTo(w * 0.65f, 0f)
        cubicTo(w * 0.60f, h * 0.4f, w * 0.68f, h * 0.7f, w * 0.62f, h)
    }
    drawPath(
        natHighwayPath,
        color = Color(0xFFFFFFFF),
        style = Stroke(width = 11f, cap = StrokeCap.Round)
    )

    // Connecting Cross-Roads (San Francisco/Southwoods - San Antonio - Poblacion)
    drawLine(
        color = Color(0xFFFFFFFF),
        start = Offset(0f, h * 0.32f),
        end = Offset(w * 0.85f, h * 0.36f),
        strokeWidth = 7f
    )
    drawLine(
        color = Color(0xFFFFFFFF),
        start = Offset(0f, h * 0.65f),
        end = Offset(w * 0.85f, h * 0.62f),
        strokeWidth = 7f
    )

    // 3. Coordinate mapping from Biñan bounding box
    // Lat range: 14.28 to 14.36, Lng range: 121.02 to 121.095
    fun toMapPoint(lat: Double, lng: Double): Offset {
        val normX = ((lng - 121.025) / (121.095 - 121.025)).coerceIn(0.1, 0.9).toFloat()
        // Invert Y since latitude increases northward
        val normY = (1f - ((lat - 14.285) / (14.365 - 14.285)).coerceIn(0.1, 0.9)).toFloat()
        return Offset(normX * w, normY * h)
    }

    val originPoint = toMapPoint(originLoc.latitude, originLoc.longitude)
    val destPoint = toMapPoint(destLoc.latitude, destLoc.longitude)

    // 4. Delivery Route Polyline
    val midControlX = (originPoint.x + destPoint.x) / 2f + (if (originPoint.y > destPoint.y) 30f else -30f)
    val midControlY = (originPoint.y + destPoint.y) / 2f

    val routePath = Path().apply {
        moveTo(originPoint.x, originPoint.y)
        quadraticTo(midControlX, midControlY, destPoint.x, destPoint.y)
    }

    // Shadow line
    drawPath(
        routePath,
        color = Color(0x33000000),
        style = Stroke(width = 9f, cap = StrokeCap.Round)
    )
    // Active delivery track line (Pink Rose Gradient)
    drawPath(
        routePath,
        brush = Brush.linearGradient(
            colors = listOf(ElayaPinkPrimary, ElayaRoseCrimson),
            start = originPoint,
            end = destPoint
        ),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )

    // 5. Origin Marker (Florist Shop)
    drawCircle(
        color = ElayaPinkPrimary.copy(alpha = 0.25f),
        radius = 18f,
        center = originPoint
    )
    drawCircle(
        color = ElayaRoseDark,
        radius = 10f,
        center = originPoint
    )
    drawCircle(
        color = Color.White,
        radius = 4f,
        center = originPoint
    )

    // 6. Destination Marker (Customer in Barangay)
    drawCircle(
        color = StatusSuccess.copy(alpha = 0.25f),
        radius = 18f,
        center = destPoint
    )
    drawCircle(
        color = StatusSuccess,
        radius = 10f,
        center = destPoint
    )
    drawCircle(
        color = Color.White,
        radius = 4f,
        center = destPoint
    )

    // 7. Live Rider Marker (Moving on quadratic Bezier curve)
    val t = progress.coerceIn(0f, 1f)
    val riderX = (1 - t) * (1 - t) * originPoint.x + 2 * (1 - t) * t * midControlX + t * t * destPoint.x
    val riderY = (1 - t) * (1 - t) * originPoint.y + 2 * (1 - t) * t * midControlY + t * t * destPoint.y
    val riderPoint = Offset(riderX, riderY)

    if (orderStatus == "Out for Delivery" || orderStatus == "Ready for Delivery") {
        // Radar pulse
        drawCircle(
            color = ElayaRoseCrimson.copy(alpha = 0.2f),
            radius = 24f,
            center = riderPoint
        )
        drawCircle(
            color = Color.White,
            radius = 13f,
            center = riderPoint
        )
        drawCircle(
            color = ElayaRoseCrimson,
            radius = 9f,
            center = riderPoint
        )
    }
}
