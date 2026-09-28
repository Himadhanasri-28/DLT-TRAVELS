package com.example.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.BusinessProfile
import com.example.ui.theme.*
import java.io.BufferedReader
import java.io.InputStreamReader

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebsitePreviewScreen(
    profile: BusinessProfile,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Live Website", "HTML", "CSS", "JS", "Guide")

    // Load assets code asynchronously / remember
    val htmlCode by remember {
        mutableStateOf(loadAssetFile(context, "website/index.html"))
    }
    val cssCode by remember {
        mutableStateOf(loadAssetFile(context, "website/style.css"))
    }
    val jsCode by remember {
        mutableStateOf(loadAssetFile(context, "website/script.js"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
            .testTag("website_preview_screen")
    ) {
        // Tab selector
        Surface(
            color = Navy900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "🌐 Standalone Website & Code Exporter",
                    color = Amber500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Navy900,
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Amber500 else Slate400
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Live WebView of index.html
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                }
                                webViewClient = WebViewClient()
                                loadUrl("file:///android_asset/website/index.html")
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp)
                            .testTag("website_webview")
                    )
                }
            }
            1 -> {
                CodeViewer(
                    title = "index.html",
                    code = htmlCode,
                    onCopy = { copyToClipboard(context, "HTML Code", htmlCode) }
                )
            }
            2 -> {
                CodeViewer(
                    title = "style.css",
                    code = cssCode,
                    onCopy = { copyToClipboard(context, "CSS Code", cssCode) }
                )
            }
            3 -> {
                CodeViewer(
                    title = "script.js",
                    code = jsCode,
                    onCopy = { copyToClipboard(context, "JavaScript Code", jsCode) }
                )
            }
            4 -> {
                InstructionsView(profile = profile)
            }
        }
    }
}

@Composable
private fun CodeViewer(
    title: String,
    code: String,
    onCopy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 90.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Navy900
            )

            Button(
                onClick = onCopy,
                colors = ButtonDefaults.buttonColors(containerColor = Amber500, contentColor = Navy900),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy Full Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            shape = RoundedCornerShape(10.dp)
        ) {
            val vScroll = rememberScrollState()
            val hScroll = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(vScroll)
                    .horizontalScroll(hScroll)
            ) {
                Text(
                    text = code,
                    color = Slate200,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun InstructionsView(profile: BusinessProfile) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 90.dp)
            .verticalScroll(scrollState)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Website Setup & Deployment Guide",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Navy900
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Follow these simple steps to launch your father's car travel business website:",
                    fontSize = 13.sp,
                    color = Slate600
                )

                Spacer(modifier = Modifier.height(14.dp))

                GuideStep(
                    step = "1",
                    title = "Replace Placeholders",
                    desc = "Search and replace [BUSINESS NAME], [PHONE NUMBER], [WHATSAPP NUMBER], [SERVICE AREA], and [ADDRESS] in index.html, style.css, and script.js."
                )

                GuideStep(
                    step = "2",
                    title = "Test Locally",
                    desc = "Simply double click index.html to open in Chrome or Edge. No software installation needed!"
                )

                GuideStep(
                    step = "3",
                    title = "Host Free Online",
                    desc = "Upload to GitHub Pages, Netlify, or Vercel. You will get a free live URL (e.g., https://yourtravels.netlify.app) with HTTPS enabled."
                )

                GuideStep(
                    step = "4",
                    title = "Connect Custom Domain (Optional)",
                    desc = "Buy a domain like yournamecabs.com for ₹500-₹800/yr and link it to your free hosting for a completely professional look."
                )
            }
        }
    }
}

@Composable
private fun GuideStep(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = Amber500,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.size(22.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(step, color = Navy900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
            Text(desc, fontSize = 12.sp, color = Slate600, lineHeight = 16.sp)
        }
    }
}

private fun loadAssetFile(context: Context, filename: String): String {
    return try {
        context.assets.open(filename).use { stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        }
    } catch (e: Exception) {
        "Error loading $filename: ${e.message}"
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
}
