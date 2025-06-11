package com.example.currencyconverterpro.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.currencyconverterpro.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, viewModel: SplashViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    // Saat aplikasi pertama kali dibuka, ia akan memeriksa status login dari DataStore.
    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.popBackStack()
        if (isLoggedIn) {
            // Jika sudah login, pengguna akan langsung diarahkan ke Halaman Utama (Home).
            navController.navigate(Routes.MAIN)
        } else {
            // Jika belum, pengguna akan diarahkan ke Halaman Login.
            navController.navigate(Routes.AUTH)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Currency Converter Pro", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}