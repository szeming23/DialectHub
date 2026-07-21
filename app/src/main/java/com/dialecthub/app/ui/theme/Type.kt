package com.dialecthub.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val base = Typography()

// A larger, bolder style reserved for the Hanji shown on flashcards.
val DialectHubTypography = base.copy(
    headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold)
)

val FlashcardHanjiStyle: TextStyle = TextStyle(
    fontSize = 56.sp,
    fontWeight = FontWeight.Bold
)
