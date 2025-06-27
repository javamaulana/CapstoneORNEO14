package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.currencyconverterpro.R
import com.example.currencyconverterpro.ui.navigation.Routes

@Composable
fun ProfileScreen(mainNavController: NavController, viewModel: ProfileViewModel) {
    val user by viewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            val avatarImage = painterResource(
                id = when (user?.gender) {
                    "Laki-laki" -> R.drawable.ic_avatar_male
                    "Perempuan" -> R.drawable.ic_avatar_female
                    else -> R.drawable.ic_avatar_male
                }
            )

            Image(
                painter = avatarImage,
                contentDescription = "Avatar Profil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menampilkan data dari pengguna
            user?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Biodata Pengguna",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    user?.let {
                        // Data diambil dari Room database
                        ProfileInfoRow(icon = Icons.Default.Person, label = "Nama Lengkap", value = it.name)
                        ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = it.email)
                        val genderIcon = if (it.gender == "Laki-laki") Icons.Default.Man else Icons.Default.Woman
                        ProfileInfoRow(icon = genderIcon, label = "Jenis Kelamin", value = it.gender)
                    }
                }
            }
        }

        // Tombol Logout di bagian bawah
        OutlinedButton(
            onClick = {
                // Pengguna menekan tombol "Logout"
                viewModel.logout()
                // Aplikasi akan mengarahkan pengguna kembali ke LoginScreen
                mainNavController.navigate(Routes.AUTH) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Text("LOGOUT")
        }
    }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}