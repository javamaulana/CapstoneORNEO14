package com.example.currencyconverterpro.ui.auth

// <-- Tambahkan import yang dibutuhkan
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.currencyconverterpro.ui.navigation.Routes

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val genderOptions = listOf("Laki-laki", "Perempuan")
    var selectedGender by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    // <-- 1. Buat SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.RegistrationSuccess -> {
                // <-- 2. Ganti Toast dengan Snackbar
                snackbarHostState.showSnackbar(
                    message = "Registrasi berhasil! Silakan login.",
                    duration = SnackbarDuration.Short
                )
                navController.popBackStack()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                // <-- 2. Ganti Toast dengan Snackbar
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
        // Form untuk mendaftarkan pengguna baru (misalnya: nama, email, password).
        Column(
            // <-- 4. Terapkan padding dari Scaffold
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Buat Akun Baru", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Jenis Kelamin", style = MaterialTheme.typography.bodyLarge)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                genderOptions.forEach { gender ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (gender == selectedGender),
                                onClick = { selectedGender = gender }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender }
                        )
                        Text(text = gender, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.register(name, email, password, selectedGender) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("REGISTER")
            }
        }
    }
}