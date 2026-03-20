package com.kaizen.skywear.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// LightColorScheme
private val LightColorScheme = lightColorScheme(
    //Primary - SkyBlue
    primary = SkyBlue40,
    onPrimary = Neutral99,
    primaryContainer = SkyBlue90,
    onPrimaryContainer = SkyBlue10,

    //Secondary - WarmCoral
    secondary = Coral40,
    onSecondary = Neutral99,
    secondaryContainer = Coral90,
    onSecondaryContainer = Coral10,

    //Tertiary - SoftGreen
    tertiary = WarmGreen40,
    onTertiary = Neutral99,
    tertiaryContainer = WarmGreen90,
    onTertiaryContainer = WarmGreen10,

    //Error
    error = ErrorRed,
    onError = Neutral99,
    errorContainer = ErrorRedBg,
    onErrorContainer = Color(0xFF410002),

    // Background & Surface
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,

    //Inverse(스낵바 등)
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = SkyBlue80,
)

// DarkColorScheme
private val DarkColorScheme = darkColorScheme(
    // Primary - SkyBlue (다크에선 밝게)
    primary = SkyBlue80,
    onPrimary = SkyBlue20,
    primaryContainer = SkyBlue30,
    onPrimaryContainer = SkyBlue90,

    // Secondary - WarmCoral
    secondary = Coral80,
    onSecondary = Coral20,
    secondaryContainer = Coral30,
    onSecondaryContainer = Coral90,

    // Tertiary - SoftGreen
    tertiary = WarmGreen80,
    onTertiary = WarmGreen10,
    tertiaryContainer = WarmGreen20,
    onTertiaryContainer = WarmGreen90,

    // Error
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Background & Surface
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVar,
    onSurfaceVariant = NeutralVariant80,
    outline = DarkOutline,
    outlineVariant = NeutralVariant30,

    // Inverse
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    inversePrimary = SkyBlue40,
)

// SkyWear Theme Entry Point
@Composable
fun SkyWearTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Android 12+ Dynamic Color (Material You) — 선택적 활성화
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
){
    val colorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 상태바 색상을 테마에 맞게 동기화
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SkyWearTypography,
        content = content
    )
}

// Custom Color Extensions
// MaterialTheme에서 직접 접근 불가한 커스텀 컬러 확장
// 사용 예: MaterialTheme.extraColors.koreaBlue


data class SkyWearExtraColors(
    val koreaRed: androidx.compose.ui.graphics.Color = KoreaRed,
    val japanBlue: androidx.compose.ui.graphics.Color = JapanBlue,
    val tempScorching: androidx.compose.ui.graphics.Color = TempScorching,
    val tempHot: androidx.compose.ui.graphics.Color = TempHot,
    val tempWarm: androidx.compose.ui.graphics.Color = TempWarm,
    val tempMild: androidx.compose.ui.graphics.Color = TempMild,
    val tempCool: androidx.compose.ui.graphics.Color = TempCool,
    val tempChilly: androidx.compose.ui.graphics.Color = TempChilly,
    val tempCold: androidx.compose.ui.graphics.Color = TempCold,
    val tempFreezing: androidx.compose.ui.graphics.Color = TempFreezing,
    val successGreen: androidx.compose.ui.graphics.Color = SuccessGreen,
    val successBg: androidx.compose.ui.graphics.Color = SuccessGreenBg,
    val warningAmber: androidx.compose.ui.graphics.Color = WarningAmber,
    val warningBg: androidx.compose.ui.graphics.Color = WarningAmberBg,
    val infoBlue: androidx.compose.ui.graphics.Color = InfoBlue,
    val infoBg: androidx.compose.ui.graphics.Color = InfoBlueBg,
)

val LocalExtraColors = staticCompositionLocalOf { SkyWearExtraColors() }

// MaterialTheme 확장 프로퍼티 — 사용: MaterialTheme.extraColors.koreaBlue
val MaterialTheme.extraColors: SkyWearExtraColors
    @Composable get() = LocalExtraColors.current

// Temparature Color Helper
// 사용: getTemperatureColor(temp) → 해당 온도의 색상 반환
// Phase 3-1 (Outfit Algorithm) 에서 활용

fun getTemperatureColor(celsius: Double): androidx.compose.ui.graphics.Color = when {
    celsius >= 28 -> TempScorching
    celsius >= 23 -> TempHot
    celsius >= 17 -> TempWarm
    celsius >= 12 -> TempMild
    celsius >= 9 -> TempCool
    celsius >= 5 -> TempChilly
    celsius >= 0 -> TempCold
    else -> TempFreezing
}
