package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEnquiry
import com.example.data.BusinessProfile
import com.example.ui.BookingScreen
import com.example.ui.HomeScreen
import com.example.ui.SettingsScreen
import com.example.ui.WebsitePreviewScreen
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

enum class NavScreen(val label: String) {
    HOME("Home"),
    BOOKING("Book"),
    WEBSITE("Website"),
    SETTINGS("Contact")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer() {
    val context = LocalContext.current
    var businessProfile by remember {
        mutableStateOf(BusinessProfile.loadFromPrefs(context))
    }

    var currentScreen by remember { mutableStateOf(NavScreen.HOME) }
    var prefilledTripType by remember { mutableStateOf<String?>(null) }

    fun dialPhone() {
        val number = businessProfile.phoneNumber.trim()
        val cleanNumber = number.replace(" ", "").replace("-", "")
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$cleanNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open dialer: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(customText: String? = null) {
        val number = businessProfile.whatsappNumber.trim()
        val cleanNumber = number.replace("+", "").replace(" ", "").replace("-", "")
        val message = customText ?: "Hello! I would like to enquire about booking your Maruti Suzuki Dzire car."
        val url = "https://wa.me/$cleanNumber?text=${Uri.encode(message)}"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = businessProfile.businessName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Navy900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Maruti Suzuki Dzire • ${businessProfile.serviceArea}",
                            fontSize = 11.sp,
                            color = Slate600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { dialPhone() },
                        modifier = Modifier.testTag("appbar_call_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = CallBlue
                        )
                    }
                    IconButton(
                        onClick = { openWhatsApp() },
                        modifier = Modifier.testTag("appbar_whatsapp_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            tint = WhatsAppGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Navy900
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == NavScreen.HOME,
                    onClick = { currentScreen = NavScreen.HOME },
                    icon = {
                        Icon(
                            if (currentScreen == NavScreen.HOME) Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Navy900,
                        selectedTextColor = Navy900,
                        indicatorColor = Amber500
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavScreen.BOOKING,
                    onClick = {
                        prefilledTripType = null
                        currentScreen = NavScreen.BOOKING
                    },
                    icon = {
                        Icon(
                            if (currentScreen == NavScreen.BOOKING) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                            contentDescription = "Book"
                        )
                    },
                    label = { Text("Book", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Navy900,
                        selectedTextColor = Navy900,
                        indicatorColor = Amber500
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavScreen.WEBSITE,
                    onClick = { currentScreen = NavScreen.WEBSITE },
                    icon = {
                        Icon(
                            if (currentScreen == NavScreen.WEBSITE) Icons.Filled.Language else Icons.Outlined.Language,
                            contentDescription = "Website"
                        )
                    },
                    label = { Text("Website", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Navy900,
                        selectedTextColor = Navy900,
                        indicatorColor = Amber500
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == NavScreen.SETTINGS,
                    onClick = { currentScreen = NavScreen.SETTINGS },
                    icon = {
                        Icon(
                            if (currentScreen == NavScreen.SETTINGS) Icons.Filled.ContactPhone else Icons.Outlined.ContactPhone,
                            contentDescription = "Contact"
                        )
                    },
                    label = { Text("Contact", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Navy900,
                        selectedTextColor = Navy900,
                        indicatorColor = Amber500
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavScreen.HOME -> {
                    HomeScreen(
                        profile = businessProfile,
                        onCallClicked = { dialPhone() },
                        onWhatsAppClicked = { openWhatsApp() },
                        onNavigateToBooking = { serviceTitle ->
                            prefilledTripType = serviceTitle
                            currentScreen = NavScreen.BOOKING
                        }
                    )
                }

                NavScreen.BOOKING -> {
                    BookingScreen(
                        profile = businessProfile,
                        initialTripType = prefilledTripType,
                        onSendWhatsApp = { enquiry: BookingEnquiry ->
                            val msg = enquiry.toFormattedWhatsAppMessage(businessProfile.businessName)
                            openWhatsApp(msg)
                        },
                        onCallPhone = { dialPhone() }
                    )
                }

                NavScreen.WEBSITE -> {
                    WebsitePreviewScreen(
                        profile = businessProfile
                    )
                }

                NavScreen.SETTINGS -> {
                    SettingsScreen(
                        currentProfile = businessProfile,
                        onSaveProfile = { updated ->
                            businessProfile = updated
                            updated.saveToPrefs(context)
                        },
                        onCallPhone = { dialPhone() },
                        onWhatsAppChat = { openWhatsApp() }
                    )
                }
            }
        }
    }
}
