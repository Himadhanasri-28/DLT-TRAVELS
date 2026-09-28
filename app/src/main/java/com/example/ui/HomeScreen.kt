package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessProfile
import com.example.ui.components.DzireCarCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: BusinessProfile,
    onCallClicked: () -> Unit,
    onWhatsAppClicked: () -> Unit,
    onNavigateToBooking: (prefillTripType: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Header
        item {
            HeroHeaderSection(
                profile = profile,
                onCallClicked = onCallClicked,
                onWhatsAppClicked = onWhatsAppClicked,
                onBookClicked = { onNavigateToBooking(null) }
            )
        }

        // Dzire Car Spotlight
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                DzireCarCard(
                    onBookNowClicked = { onNavigateToBooking("Maruti Dzire Special") }
                )
            }
        }

        // About Us Section
        item {
            AboutUsSection(profile = profile)
        }

        // Services Catalog
        item {
            ServicesSection(
                onSelectService = { serviceName ->
                    onNavigateToBooking(serviceName)
                }
            )
        }

        // Why Choose Us Section
        item {
            WhyChooseUsSection()
        }

        // Customer Testimonials Section
        item {
            TestimonialsSection()
        }

        // Direct Contact Banner
        item {
            ContactSummarySection(
                profile = profile,
                onCallClicked = onCallClicked,
                onWhatsAppClicked = onWhatsAppClicked
            )
        }
    }
}

@Composable
private fun HeroHeaderSection(
    profile: BusinessProfile,
    onCallClicked: () -> Unit,
    onWhatsAppClicked: () -> Unit,
    onBookClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Navy900, Navy800)
                )
            )
            .padding(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Location Badge
            Surface(
                color = Amber500.copy(alpha = 0.15f),
                shape = RoundedCornerShape(50),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Amber500, Amber600)))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = Amber500,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = profile.serviceArea,
                        color = Amber500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = profile.businessName,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Maruti Suzuki Dzire Tour & Taxi Service",
                color = Amber500,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Safe, punctual, and reliable car travel for local city trips, outstation tours, and airport pickup/drop.",
                color = Slate400,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3 Hero CTA Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBookClicked,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .testTag("hero_book_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Amber500,
                        contentColor = Navy900
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Book Ride", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onCallClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("hero_call_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CallBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onWhatsAppClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("hero_whatsapp_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AboutUsSection(profile: BusinessProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Amber100,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("👋", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ABOUT OUR SERVICE",
                        color = Amber600,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Your Trusted Travel Partner",
                        color = Navy900,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We are an experienced, family-run car travel and taxi service operating with our well-maintained Maruti Suzuki Dzire sedan across ${profile.serviceArea}.",
                color = Slate600,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Unlike impersonal ride-hailing apps that suffer from last-minute cancellations, dynamic surge pricing, or careless drivers, we offer guaranteed on-time pickups, spotless clean interiors, and direct personal accountability for every trip.",
                color = Slate600,
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trust Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate100, RoundedCornerShape(12.dp))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(number = "500+", label = "Trips Done")
                VerticalDivider(modifier = Modifier.height(36.dp), color = Slate200)
                StatColumn(number = "100%", label = "On-Time")
                VerticalDivider(modifier = Modifier.height(36.dp), color = Slate200)
                StatColumn(number = "4.9 ★", label = "Rating")
                VerticalDivider(modifier = Modifier.height(36.dp), color = Slate200)
                StatColumn(number = "24x7", label = "Available")
            }
        }
    }
}

@Composable
private fun StatColumn(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            color = Navy900,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Slate600,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ServicesSection(onSelectService: (String) -> Unit) {
    val services = listOf(
        ServiceItem(
            title = "Local City Trips",
            desc = "Flexible 4hr/40km or 8hr/80km packages for shopping, meetings, and local errands.",
            icon = Icons.Default.LocationCity,
            bullet = "No surge & multiple stops"
        ),
        ServiceItem(
            title = "Outstation Trips",
            desc = "Smooth highway travel for weekend getaways, pilgrimages, and hill station vacations.",
            icon = Icons.Default.AltRoute,
            bullet = "Comfortable sedan on highways"
        ),
        ServiceItem(
            title = "Airport Pickup & Drop",
            desc = "24x7 punctual airport transfers. Zero anxiety with advance confirmed booking.",
            icon = Icons.Default.FlightTakeoff,
            bullet = "Doorstep luggage assistance"
        ),
        ServiceItem(
            title = "One-Way Drops",
            desc = "Affordable one-way intercity drops without paying return vehicle charges.",
            icon = Icons.Default.ArrowForward,
            bullet = "Pay only for distance traveled"
        ),
        ServiceItem(
            title = "Round Trips",
            desc = "Same day or multi-day return journeys with the car dedicated at your disposal.",
            icon = Icons.Default.Sync,
            bullet = "Flexible halts & sight-seeing"
        ),
        ServiceItem(
            title = "Custom Travel",
            desc = "Bespoke itineraries for weddings, VIP guests, medical trips, and corporate travel.",
            icon = Icons.Default.Star,
            bullet = "Customized rate & timing"
        )
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "OUR SERVICES",
            color = Amber600,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Travel Solutions Tailored For You",
            color = Navy900,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        services.forEach { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Navy100, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = service.icon,
                            contentDescription = null,
                            tint = Navy900,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.title,
                            color = Navy900,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = service.desc,
                            color = Slate600,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = service.bullet,
                                color = Slate700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalButton(
                        onClick = { onSelectService(service.title) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun WhyChooseUsSection() {
    val reasons = listOf(
        Pair("Clean & Well-Maintained", "Deep sanitized, vacuumed daily, and pristine AC cabin."),
        Pair("Comfortable Journey", "Sedan suspension with comfortable legroom and smooth driving."),
        Pair("Professional Service", "Polite, sober, experienced driver with verified credentials."),
        Pair("Transparent Pricing", "Clear fixed rates agreed upfront. Zero surprise surcharge."),
        Pair("On-Time Pickup", "We reach 10-15 minutes prior to scheduled pickup time.")
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Navy900),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "WHY CHOOSE US",
                color = Amber500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "The 5 Promises We Keep",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            reasons.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Amber500.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Amber500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = pair.first,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pair.second,
                            color = Slate400,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TestimonialsSection() {
    val testimonials = listOf(
        Triple(
            "Suresh Varma",
            "Family Tour to Araku Valley",
            "\"Booked DLT Travels for a 2-day family trip to Araku Valley. The Maruti Dzire was spotless and the AC was chilling throughout. Safe, defensive driving on the ghat roads made our family feel very secure. Excellent service!\""
        ),
        Triple(
            "Dr. Ananya Rao",
            "Airport Transfer • MVP Colony",
            "\"I frequently book early morning rides to Visakhapatnam Airport (VTZ) for business flights. The driver is consistently 10 minutes early, courteous, and helps with all luggage. No cancellation worries like app cabs!\""
        ),
        Triple(
            "K. Rajesh",
            "Corporate Travel • Gajuwaka",
            "\"Used DLT Travels for whole-day local meetings covering Vizag city and Gajuwaka industrial zone. Transparent pricing, no surprise surge fees, and very smooth ride in the Dzire sedan. Truly professional!\""
        ),
        Triple(
            "P. Venkat & Family",
            "Temple Pilgrimage • Simhachalam",
            "\"We booked a pilgrimage round trip to Simhachalam and Annavaram for our elderly parents. The driver was exceptionally patient, polite, and drove with extreme care. My parents were very comfortable throughout the journey.\""
        )
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "CUSTOMER REVIEWS",
            color = Amber600,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "What Our Passengers Say",
            color = Navy900,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        testimonials.forEach { (name, tag, quote) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "★★★★★",
                            color = Amber500,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = Amber50,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "Verified Ride",
                                color = Amber600,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = quote,
                        color = Slate700,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Navy900,
                            shape = CircleShape,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = name.take(2).uppercase(),
                                    color = Amber500,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = name,
                                color = Navy900,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tag,
                                color = Slate600,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactSummarySection(
    profile: BusinessProfile,
    onCallClicked: () -> Unit,
    onWhatsAppClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DIRECT CONTACT & BOOKING",
                color = Amber600,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Ready When You Are",
                color = Navy900,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Call or text for immediate availability check and quick rate quote.",
                color = Slate600,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCallClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = CallBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Directly", color = Navy900, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }

                Button(
                    onClick = onWhatsAppClicked,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
    }
}

private val Navy100 = Color(0xFFE2E8F0)

private data class ServiceItem(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val bullet: String
)
