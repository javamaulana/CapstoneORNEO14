package com.example.currencyconverterpro.ui.splash

// <-- Tambahkan import yang dibutuhkan
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.currencyconverterpro.R // <-- Pastikan import R sudah ada
import com.example.currencyconverterpro.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, viewModel: SplashViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

    LaunchedEffect(key1 = true) {
        delay(2000)
        navController.popBackStack()
        if (isLoggedIn) {
            navController.navigate(Routes.MAIN)
        } else {
            navController.navigate(Routes.AUTH)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // <-- Kita gunakan Column untuk menyusun gambar dan teks secara vertikal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // <-- Tampilkan gambar dari drawable
            Image(
                painter = painterResource(id = R.drawable.app_splash), // <-- GANTI app_logo dengan nama file Anda
                contentDescription = "App Logo", // <-- Deskripsi untuk aksesibilitas
                modifier = Modifier.size(120.dp) // <-- Atur ukuran gambar sesuai kebutuhan
            )

            // <-- Beri jarak antara gambar dan teks
            Spacer(modifier = Modifier.height(24.dp))

            // <-- Teks yang sudah ada sebelumnya
            Text(text = "Currency Converter Pro", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}