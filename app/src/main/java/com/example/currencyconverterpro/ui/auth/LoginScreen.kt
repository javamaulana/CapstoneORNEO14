package com.example.currencyconverterpro.ui.auth

// <-- Tambahkan import yang dibutuhkan untuk Scaffold dan Snackbar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.currencyconverterpro.ui.navigation.Routes

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // <-- 1. Buat SnackbarHostState untuk mengontrol Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // Pengguna diarahkan ke HomeScreen.
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.AUTH) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                // <-- 2. Ganti Toast dengan memanggil Snackbar
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // <-- 3. Bungkus layout dengan Scaffold
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            // <-- 4. Terapkan padding dari Scaffold
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Menerapkan padding agar konten tidak tertutup
                .padding(16.dp),      // Padding asli Anda
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.login(email, password) }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                // Pengguna dapat menekan tombol "Register" untuk diarahkan ke RegisterScreen.
                navController.navigate(Routes.REGISTER)
            }) {
                Text("Belum punya akun? Register")
            }
        }
    }
}