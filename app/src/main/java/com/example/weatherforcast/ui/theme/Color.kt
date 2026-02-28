package com.example.weatherforcast.ui.theme

import androidx.compose.ui.graphics.Color

// --- Your Original Colors (Fixed) ---
val BlueDark = Color(0xFF2A4A62)
val BluePrimary = Color(0xFF355872)
val BlueSecondary = Color(0xFF4A7A9B)
val BlueAccent = Color(0xFF7AAACE)
val Grey = Color(0xFF898989) // Fixed: Added FF at the start

// --- New Weather Palette ---

// Text & Neutral Colors
val GreyLight = Color(0xFFBDBDBD)
val TextWhite = Color(0xFFFFFFFF)
val TextLightGrey = Color(0xFFD1D1D1)

// Weather State Colors
val SunYellow = Color(0xFFFFD54F)  // For Sunny days
val RainTeal = Color(0xFF4DD0E1)   // For Rainy days
val StormPurple = Color(0xFF7E57C2) // For Stormy nights
val SnowWhite = Color(0xFFE0F7FA)  // For Snowing

// Warning/Alerts
val AlertOrange = Color(0xFFFF7043)
val ErrorRed = Color(0xFFE57373)

// 20% opacity White
val GlassWhite = Color(0x33FFFFFF)

// 10% opacity White (very subtle)
val GlassWhiteLight = Color(0x1AFFFFFF)

// A soft stroke color for the "edge" of the glass
val GlassStroke = Color(0x66FFFFFF)