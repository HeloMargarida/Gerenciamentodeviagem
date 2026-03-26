package com.senac.travelapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// 🔵 CORES PERSONALIZADAS
private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = GrayBackground
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    background = GrayBackground
)

@Composable
fun TravelAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 🔥 DESATIVADO pra manter suas cores
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}