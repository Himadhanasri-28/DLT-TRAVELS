package com.example.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessProfile
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentProfile: BusinessProfile,
    onSaveProfile: (BusinessProfile) -> Unit,
    onCallPhone: () -> Unit,
    onWhatsAppChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var businessName by remember { mutableStateOf(currentProfile.businessName) }
    var phoneNumber by remember { mutableStateOf(currentProfile.phoneNumber) }
    var whatsappNumber by remember { mutableStateOf(currentProfile.whatsappNumber) }
    var serviceArea by remember { mutableStateOf(currentProfile.serviceArea) }
    var address by remember { mutableStateOf(currentProfile.address) }
    var baseRate by remember { mutableStateOf(currentProfile.baseRatePerKm) }
    var minKm by remember { mutableStateOf(currentProfile.minOutstationKm) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .testTag("settings_screen_content"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)
    ) {
        item {
            // Business Contact Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "BUSINESS CONTACT & DETAILS",
                        color = Amber500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Customize Your Taxi Business",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Update the placeholder details below with your father's real business name, phone, WhatsApp number, and service location.",
                        color = Slate400,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            // Quick Test Direct Buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Instant Contact Links",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onCallPhone,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CallBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onWhatsAppChat,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            // Edit Profile Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Edit Business Profile & Placeholders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Navy900
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text("Business Name [BUSINESS NAME]") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Slate600) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Calling Number [PHONE NUMBER]") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CallBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("WhatsApp Number [WHATSAPP NUMBER]") },
                        leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = serviceArea,
                        onValueChange = { serviceArea = it },
                        label = { Text("Operational City / Service Area [SERVICE AREA]") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Amber600) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Base Garage / Office Address [ADDRESS]") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Slate600) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = baseRate,
                            onValueChange = { baseRate = it },
                            label = { Text("Base Outstation Rate") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = minKm,
                            onValueChange = { minKm = it },
                            label = { Text("Min Outstation Km") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val updated = currentProfile.copy(
                                businessName = businessName,
                                phoneNumber = phoneNumber,
                                whatsappNumber = whatsappNumber,
                                serviceArea = serviceArea,
                                address = address,
                                baseRatePerKm = baseRate,
                                minOutstationKm = minKm
                            )
                            onSaveProfile(updated)
                            Toast.makeText(context, "Business Details Updated!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_business_profile_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Navy900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Business Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
