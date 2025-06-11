package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.currencyconverterpro.data.db.FavoritePair
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: FavoritesViewModel) {
    val favorites by viewModel.favorites.collectAsState()
    val rates by viewModel.rates.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<FavoritePair?>(null) }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Favorit") },
            text = { Text("Apakah Anda yakin ingin menghapus pasangan ${itemToDelete!!.fromCurrency} -> ${itemToDelete!!.toCurrency} dari favorit?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(itemToDelete!!)
                        showDeleteDialog = false
                        itemToDelete = null
                    }
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorit Saya") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            if (favorites.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorit Kosong",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Daftar Favorit Masih Kosong",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            "Tambahkan pasangan mata uang dari halaman konverter.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favorites, key = { it.id }) { pair ->
                        val rateKey = "${pair.fromCurrency}-${pair.toCurrency}"
                        val rate = rates[rateKey]
                        FavoriteCard(
                            pair = pair,
                            rate = rate,
                            onClick = {
                                val routeWithArgs = "${BottomBarScreen.Converter.route}?from=${pair.fromCurrency}&to=${pair.toCurrency}"
                                navController.navigate(routeWithArgs) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    // PERBAIKAN FINAL: Pastikan state tidak dikembalikan agar argumen baru digunakan
                                    restoreState = false
                                }
                            },
                            onDeleteClick = {
                                itemToDelete = pair
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(
    pair: FavoritePair,
    rate: Double?,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("in", "ID"))) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fromCountryCode = CurrencyDataMapper.getCountryCode(pair.fromCurrency)
                    if (fromCountryCode != null) {
                        AsyncImage(
                            model = CurrencyDataMapper.getFlagUrl(fromCountryCode),
                            contentDescription = "Bendera ${pair.fromCurrency}",
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Column {
                        Text(pair.fromCurrency, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("1", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val toCountryCode = CurrencyDataMapper.getCountryCode(pair.toCurrency)
                    if (toCountryCode != null) {
                        AsyncImage(
                            model = CurrencyDataMapper.getFlagUrl(toCountryCode),
                            contentDescription = "Bendera ${pair.toCurrency}",
                            modifier = Modifier.size(32.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Column {
                        Text(pair.toCurrency, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (rate != null && rate > 0.0) {
                            Text(
                                text = numberFormatter.format(rate),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "memuat...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Box(contentAlignment = Alignment.Center) {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Favorit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}