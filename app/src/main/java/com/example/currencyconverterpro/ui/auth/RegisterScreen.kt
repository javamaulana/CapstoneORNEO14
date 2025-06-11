package com.example.currencyconverterpro.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val genderOptions = listOf("Laki-laki", "Perempuan")
    var selectedGender by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.RegistrationSuccess -> {
                Toast.makeText(context, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                // Setelah berhasil, pengguna diarahkan kembali ke LoginScreen.
                navController.popBackStack()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Form untuk mendaftarkan pengguna baru (misalnya: nama, email, password).
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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

        // --- PERBAIKAN: Menambahkan Arrangement.Center agar pilihan di tengah ---
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