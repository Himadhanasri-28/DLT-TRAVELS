package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DzireCarCard(
    modifier: Modifier = Modifier,
    onBookNowClicked: () -> Unit = {}
) {
    var selectedFeatureIndex by remember { mutableIntStateOf(0) }

    val features = listOf(
        CarFeature("Sedan Comfort", "Plush dual-tone beige interior, rear center armrest with cup holders, ample legroom.", Icons.Default.DirectionsCar),
        CarFeature("378L Boot", "Deep trunk accommodates 3-4 large airport trolley bags and family luggage.", Icons.Default.Luggage),
        CarFeature("Chilled AC", "Powerful automatic climate control with dedicated rear passenger AC vents.", Icons.Default.Air),
        CarFeature("Safety First", "Dual front airbags, ABS with EBD, ISOFIX child mounts, speed alert reminder.", Icons.Default.Security)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dzire_car_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Tag & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Amber500.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(50),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Amber500, Amber600)))
                ) {
                    Text(
                        text = "⭐ SEDAN CLASS • 4+1 SEATER",
                        color = Amber500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Sanitized",
                            tint = SuccessGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Daily Sanitized",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Maruti Suzuki Dzire",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "India's #1 Preferred Comfortable Tour & Taxi Sedan",
                color = Slate400,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Vector Dzire Car Canvas Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Navy800, Navy900)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                DzireCarIllustration()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Quick Spec Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecBadge(label = "Capacity", value = "4 + 1 Seats")
                SpecBadge(label = "Boot Space", value = "378 Liters")
                SpecBadge(label = "Climate", value = "Dual AC")
                SpecBadge(label = "Audio", value = "Bluetooth")
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Tabs
            Text(
                text = "Vehicle Highlights:",
                color = Slate200,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                features.forEachIndexed { index, item ->
                    val isSelected = selectedFeatureIndex == index
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedFeatureIndex = index }
                            .testTag("feature_tab_$index"),
                        color = if (isSelected) Amber500 else Navy800,
                        shape = RoundedCornerShape(10.dp),
                        border = if (isSelected) null else CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate600, Slate700)))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) Navy900 else Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.title.substringBefore(" "),
                                color = if (isSelected) Navy900 else Slate200,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Feature Description Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Navy800.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Slate700, Slate600)))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Amber500.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = features[selectedFeatureIndex].icon,
                            contentDescription = null,
                            tint = Amber500,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = features[selectedFeatureIndex].title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = features[selectedFeatureIndex].description,
                            color = Slate400,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBookNowClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("book_dzire_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Amber500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Book Dzire for Your Journey",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun DzireCarIllustration(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        val width = size.width
        val height = size.height

        // Ground shadow
        drawOval(
            color = Color(0x66000000),
            topLeft = Offset(width * 0.08f, height * 0.78f),
            size = Size(width * 0.84f, height * 0.12f)
        )

        // Dzire Sedan Body Path
        val carBody = Path().apply {
            moveTo(width * 0.12f, height * 0.58f)
            cubicTo(width * 0.12f, height * 0.50f, width * 0.18f, height * 0.48f, width * 0.25f, height * 0.48f)
            lineTo(width * 0.32f, height * 0.48f)
            // Windshield & Roof
            cubicTo(width * 0.38f, height * 0.32f, width * 0.45f, height * 0.24f, width * 0.58f, height * 0.24f)
            lineTo(width * 0.70f, height * 0.24f)
            // Front Windshield slope
            cubicTo(width * 0.75f, height * 0.24f, width * 0.80f, height * 0.38f, width * 0.85f, height * 0.46f)
            // Bonnet
            lineTo(width * 0.91f, height * 0.49f)
            cubicTo(width * 0.94f, height * 0.52f, width * 0.94f, height * 0.58f, width * 0.93f, height * 0.65f)
            lineTo(width * 0.92f, height * 0.70f)
            lineTo(width * 0.84f, height * 0.70f)
            // Front Wheel Arch
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    Offset(width * 0.72f, height * 0.58f),
                    Offset(width * 0.84f, height * 0.82f)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Underbody
            lineTo(width * 0.40f, height * 0.70f)
            // Rear Wheel Arch
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    Offset(width * 0.24f, height * 0.58f),
                    Offset(width * 0.36f, height * 0.82f)
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            lineTo(width * 0.14f, height * 0.70f)
            cubicTo(width * 0.12f, height * 0.69f, width * 0.12f, height * 0.64f, width * 0.12f, height * 0.58f)
            close()
        }

        // Fill Car Body with Pearl Metallic White gradient
        drawPath(
            path = carBody,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
                startY = height * 0.24f,
                endY = height * 0.70f
            )
        )

        // Windows Glass
        val windows = Path().apply {
            moveTo(width * 0.35f, height * 0.46f)
            lineTo(width * 0.42f, height * 0.28f)
            lineTo(width * 0.68f, height * 0.28f)
            lineTo(width * 0.80f, height * 0.46f)
            close()
        }

        drawPath(
            path = windows,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                startY = height * 0.28f,
                endY = height * 0.46f
            )
        )

        // Window Highlights & Pillar
        drawLine(
            color = Color(0xFF93C5FD),
            start = Offset(width * 0.44f, height * 0.30f),
            end = Offset(width * 0.78f, height * 0.44f),
            strokeWidth = 2.dp.toPx()
        )
        // Center Pillar (B-pillar)
        drawLine(
            color = Color(0xFF1E293B),
            start = Offset(width * 0.55f, height * 0.28f),
            end = Offset(width * 0.55f, height * 0.46f),
            strokeWidth = 3.dp.toPx()
        )

        // Headlight
        val headlight = Path().apply {
            moveTo(width * 0.88f, height * 0.52f)
            lineTo(width * 0.93f, height * 0.55f)
            lineTo(width * 0.90f, height * 0.60f)
            lineTo(width * 0.86f, height * 0.58f)
            close()
        }
        drawPath(path = headlight, color = Color(0xFFFEF08A))

        // Taillight
        val taillight = Path().apply {
            moveTo(width * 0.12f, height * 0.52f)
            lineTo(width * 0.16f, height * 0.52f)
            lineTo(width * 0.15f, height * 0.60f)
            lineTo(width * 0.12f, height * 0.60f)
            close()
        }
        drawPath(path = taillight, color = Color(0xFFEF4444))

        // Wheels
        val frontWheelCenter = Offset(width * 0.78f, height * 0.70f)
        val rearWheelCenter = Offset(width * 0.30f, height * 0.70f)
        val wheelRadius = height * 0.11f
        val rimRadius = height * 0.07f

        // Rear Wheel
        drawCircle(color = Color(0xFF0F172A), radius = wheelRadius, center = rearWheelCenter)
        drawCircle(color = Color(0xFFE2E8F0), radius = rimRadius, center = rearWheelCenter)
        drawCircle(color = Color(0xFF475569), radius = rimRadius * 0.35f, center = rearWheelCenter)

        // Front Wheel
        drawCircle(color = Color(0xFF0F172A), radius = wheelRadius, center = frontWheelCenter)
        drawCircle(color = Color(0xFFE2E8F0), radius = rimRadius, center = frontWheelCenter)
        drawCircle(color = Color(0xFF475569), radius = rimRadius * 0.35f, center = frontWheelCenter)

        // Door handles
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(width * 0.48f, height * 0.50f),
            size = Size(12.dp.toPx(), 3.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
        drawRoundRect(
            color = Color(0xFF475569),
            topLeft = Offset(width * 0.62f, height * 0.50f),
            size = Size(12.dp.toPx(), 3.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
    }
}

@Composable
private fun SpecBadge(label: String, value: String) {
    Surface(
        color = Navy800,
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Slate700, Slate600)))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Slate400,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private data class CarFeature(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
