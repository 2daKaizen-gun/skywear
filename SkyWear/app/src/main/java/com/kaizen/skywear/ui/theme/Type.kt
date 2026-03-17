package com.kaizen.skywear.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // 1. 온도 표시(크고 강조됨)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 64.sp,
        lineHeight = 72.sp,
        letterSpacing = (-0.5).sp
    ),
    // 2. 도시 이름(굵고 선명함)
    headlineMedium = TextStyle(

    ),
    // 3. 메인 코디 추천
    bodyLarge = TextStyle(

    ),
    // 4. 부가 정보 (습도나 바람 등 작은 텍스트)
    labelMedium = TextStyle(

    )
)