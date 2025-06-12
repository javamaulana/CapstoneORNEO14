package com.example.currencyconverterpro.ui.theme // Pastikan package ini sesuai dengan proyek Anda

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Palet warna default, sesuaikan jika perlu
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    surface = Color(0xFF1C1B1F),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6650a4),
    secondary = Color(0xFF625b71),
    tertiary = Color(0xFF7D5260),
    surface = Color(0xFFFFFBFE),
)

@Composable
fun CurrencyConverterProTheme( // Pastikan nama fungsi ini sama dengan yang Anda gunakan
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // ================== PERBAIKAN KRUSIAL ADA DI SINI ==================
            // Mengatur warna status bar menjadi transparan (benar: .toArgb())
            window.statusBarColor = Color.Transparent.toArgb()
            // ===================================================================

            // Memberitahu sistem agar layout aplikasi digambar di belakang status bar
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Mengatur agar ikon di status bar (jam, baterai) terlihat
            // (menjadi gelap di tema terang, dan terang di tema gelap)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan Anda punya file Typography.kt
        content = content
    )
}