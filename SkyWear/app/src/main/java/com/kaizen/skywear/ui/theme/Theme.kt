package com.kaizen.skywear.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = SkyWearTypography,
        content     = content
    )
}

