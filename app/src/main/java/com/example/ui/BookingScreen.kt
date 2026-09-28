package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEnquiry
import com.example.data.BusinessProfile
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    profile: BusinessProfile,
    initialTripType: String?,
    onSendWhatsApp: (BookingEnquiry) -> Unit,
    onCallPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var pickupLocation by remember { mutableStateOf("") }
    var destinationLocation by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf(currentDateStr) }
    var travelTime by remember { mutableStateOf("08:00 AM") }
    var passengers by remember { mutableStateOf("4 Passengers (Comfortable Sedan)") }
    var tripType by remember {
        mutableStateOf(initialTripType ?: "Outstation Round Trip")
    }
    var additionalNotes by remember { mutableStateOf("") }

    // Quick Date Selection Helper
    var showQuickDates by remember { mutableStateOf(false) }

    // Saved Enquiries History
    var savedEnquiries by remember {
        mutableStateOf(
            listOf(
                BookingEnquiry(
                    customerName = "Recent Example",
                    customerPhone = profile.phoneNumber,
                    pickupLocation = "Main City Center",
                    destination = "Airport Terminal 2",
                    travelDate = currentDateStr,
                    travelTime = "06:00 AM",
                    passengers = "3 Passengers",
                    tripType = "Airport Drop",
                    additionalNotes = "2 trolley bags"
                )
            )
        )
    }

    val tripTypeOptions = listOf(
        "Local City Trip (Hourly/Day)",
        "Outstation Round Trip",
        "Outstation One-Way",
        "Airport Drop",
        "Airport Pickup",
        "Custom Tour Itinerary"
    )

    val passengerOptions = listOf(
        "1 Passenger",
        "2 Passengers",
        "3 Passengers",
        "4 Passengers (Full Car)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .testTag("booking_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)
    ) {
        item {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ONLINE ENQUIRY & BOOKING",
                        color = Amber500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Reserve Maruti Suzuki Dzire",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fill in your travel plan. Generates instant pre-filled WhatsApp message & call request for immediate confirmation.",
                        color = Slate400,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            // Main Booking Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Traveler Details",
                        color = Navy900,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Your Full Name *") },
                        placeholder = { Text("e.g. Ramesh Kumar") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone Number
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Contact Phone / WhatsApp *") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate600) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_customer_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Trip Details",
                        color = Navy900,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Pickup
                    OutlinedTextField(
                        value = pickupLocation,
                        onValueChange = { pickupLocation = it },
                        label = { Text("Pickup Location *") },
                        placeholder = { Text("e.g. Home address, Station, Airport") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CallBlue) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pickup_location"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Destination
                    OutlinedTextField(
                        value = destinationLocation,
                        onValueChange = { destinationLocation = it },
                        label = { Text("Destination / Drop Location *") },
                        placeholder = { Text("e.g. City name, Hotel, Tourist place") },
                        leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = Amber600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_destination_location"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = travelDate,
                            onValueChange = { travelDate = it },
                            label = { Text("Travel Date *") },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Slate600) },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_travel_date"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = travelTime,
                            onValueChange = { travelTime = it },
                            label = { Text("Time") },
                            placeholder = { Text("08:00 AM") },
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("input_travel_time"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Quick Date Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = { travelDate = "Today" },
                            label = { Text("Today", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { travelDate = "Tomorrow" },
                            label = { Text("Tomorrow", fontSize = 11.sp) }
                        )
                        AssistChip(
                            onClick = { travelDate = "This Weekend" },
                            label = { Text("Weekend", fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Trip Type Dropdown / Chips
                    Text(
                        text = "Trip Type *",
                        color = Slate700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        tripTypeOptions.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowItems.forEach { option ->
                                    val isSelected = tripType == option
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { tripType = option },
                                        label = {
                                            Text(
                                                text = option.substringBefore(" ("),
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Amber500,
                                            selectedLabelColor = Navy900
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Passengers
                    Text(
                        text = "Passengers",
                        color = Slate700,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        passengerOptions.forEachIndexed { idx, opt ->
                            val isSelected = passengers.startsWith("${idx + 1}")
                            FilterChip(
                                selected = isSelected,
                                onClick = { passengers = opt },
                                label = { Text("${idx + 1} Pax", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Navy900,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Additional Notes
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text("Special Requests / Luggage Notes") },
                        placeholder = { Text("e.g. 3 large trolley bags, child traveling, return time...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_additional_notes"),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Actions
                    Button(
                        onClick = {
                            if (customerName.isBlank() || customerPhone.isBlank() || pickupLocation.isBlank() || destinationLocation.isBlank()) {
                                Toast.makeText(context, "Please fill in Name, Phone, Pickup and Destination", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val enquiry = BookingEnquiry(
                                customerName = customerName,
                                customerPhone = customerPhone,
                                pickupLocation = pickupLocation,
                                destination = destinationLocation,
                                travelDate = travelDate,
                                travelTime = travelTime,
                                passengers = passengers,
                                tripType = tripType,
                                additionalNotes = additionalNotes
                            )
                            savedEnquiries = listOf(enquiry) + savedEnquiries
                            onSendWhatsApp(enquiry)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_whatsapp_enquiry_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send Enquiry via WhatsApp",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onCallPhone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("call_to_book_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = CallBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Call Directly to Discuss Fares",
                            color = Navy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                    }
                }
            }
        }

        // Saved Enquiries Section
        if (savedEnquiries.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recent Booking Enquiries (${savedEnquiries.size})",
                    color = Navy900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(savedEnquiries) { enquiry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = enquiry.tripType,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Navy900
                            )
                            Surface(
                                color = Amber100,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = enquiry.travelDate,
                                    fontSize = 10.sp,
                                    color = Amber600,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${enquiry.pickupLocation} ➔ ${enquiry.destination}",
                            fontSize = 12.sp,
                            color = Slate700,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Dzire Sedan • ${enquiry.passengers} • ${enquiry.customerName}",
                            fontSize = 11.sp,
                            color = Slate400
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { onSendWhatsApp(enquiry) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Re-send on WhatsApp", color = WhatsAppDarkGreen, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
