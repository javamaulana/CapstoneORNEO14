package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun CurrencyCatalogScreen(navController: NavController, viewModel: ConverterViewModel) {
    val currencies by viewModel.currencies.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("Katalog Mata Uang", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(currencies.toList()) { (code, name) ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        navController.navigate("currency_detail/$code")
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val countryCode = CurrencyDataMapper.getCountryCode(code)
                        if (countryCode != null) {
                            AsyncImage(
                                model = CurrencyDataMapper.getFlagUrl(countryCode),
                                contentDescription = "Bendera $name",
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Column {
                            Text(code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}