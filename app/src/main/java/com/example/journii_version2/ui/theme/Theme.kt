package com.example.journii_version2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JourniiLightColorScheme = lightColorScheme(
    primary = DeepOceanBlue,
    onPrimary = Color.White,
    primaryContainer = OceanBlueContainerLight,
    onPrimaryContainer = OnOceanBlueContainerLight,

    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainerLight,
    onSecondaryContainer = OnEmeraldContainerLight,

    tertiary = SunsetOrange,
    onTertiary = Color.White,
    tertiaryContainer = SunsetContainerLight,
    onTertiaryContainer = OnSunsetContainerLight,

    background = WarmOffWhite,
    onBackground = CharcoalText,

    surface = WarmOffWhite,
    onSurface = CharcoalText,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

private val JourniiDarkColorScheme = darkColorScheme(
    primary = OceanBlueDark,
    onPrimary = OnOceanBlueDark,
    primaryContainer = OceanBlueContainerDark,
    onPrimaryContainer = OnOceanBlueContainerDark,

    secondary = EmeraldDark,
    onSecondary = OnEmeraldDark,
    secondaryContainer = EmeraldContainerDark,
    onSecondaryContainer = OnEmeraldContainerDark,

    tertiary = SunsetDark,
    onTertiary = OnSunsetDark,
    tertiaryContainer = SunsetContainerDark,
    onTertiaryContainer = OnSunsetContainerDark,

    background = BackgroundDark,
    onBackground = OnSurfaceDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

/**
 * Journii's app-wide theme. Deliberately does NOT support Android 12+ dynamic
 * (Material You) color — the brand palette is a first-order part of the
 * design spec, and letting the wallpaper override it would undercut "create
 * an original design language, don't copy existing apps."
 */
@Composable
fun JourniiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) JourniiDarkColorScheme else JourniiLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JourniiTypography,
        shapes = JourniiShapes,
        content = content
    )
}
