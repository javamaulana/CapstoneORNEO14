package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.util.*

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

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Favorit Saya",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Ringkasan kurs pilihan Anda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.AutoGraph,
                    contentDescription = "Ilustrasi",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(imageVector = Icons.Filled.Favorite, contentDescription = "Favorit Kosong", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Daftar Favorit Masih Kosong", style = MaterialTheme.typography.titleLarge)
                    Text("Tambahkan pasangan mata uang dari halaman konverter.", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "Ringkasan Cepat",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(favorites, key = { "summary_${it.id}" }) { pair ->
                                val rateKey = "${pair.fromCurrency}-${pair.toCurrency}"
                                SummaryFavoriteCard(pair = pair, rate = rates[rateKey]) {
                                    val routeWithArgs = "${BottomBarScreen.Converter.route}?from=${pair.fromCurrency}&to=${pair.toCurrency}"
                                    navController.navigate(routeWithArgs) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Semua Pasangan Tersimpan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(favorites, key = { it.id }) { pair ->
                    val rateKey = "${pair.fromCurrency}-${pair.toCurrency}"
                    FavoriteCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        pair = pair,
                        rate = rates[rateKey],
                        onClick = {
                            val routeWithArgs = "${BottomBarScreen.Converter.route}?from=${pair.fromCurrency}&to=${pair.toCurrency}"
                            navController.navigate(routeWithArgs) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = false
                            }
                        },
                        onDeleteClick = {
                            itemToDelete = pair
                            showDeleteDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SummaryFavoriteCard(pair: FavoritePair, rate: Double?, onClick: () -> Unit) {
    val numberFormatter = remember { DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("in", "ID"))) }
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${pair.fromCurrency}/${pair.toCurrency}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (rate != null && rate > 0.0) {
                Text(
                    text = numberFormatter.format(rate),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}

@Composable
fun FavoriteCard(
    modifier: Modifier = Modifier,
    pair: FavoritePair,
    rate: Double?,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val numberFormatter = remember { DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("in", "ID"))) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Column {
                        Text(pair.fromCurrency, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("1 ${pair.fromCurrency}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val toCountryCode = CurrencyDataMapper.getCountryCode(pair.toCurrency)
                    if (toCountryCode != null) {
                        AsyncImage(
                            model = CurrencyDataMapper.getFlagUrl(toCountryCode),
                            contentDescription = "Bendera ${pair.toCurrency}",
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Column {
                        Text(pair.toCurrency, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        if (rate != null && rate > 0.0) {
                            Text(
                                text = numberFormatter.format(rate),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
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
            Box(contentAlignment = Alignment.TopStart, modifier = Modifier.height(IntrinsicSize.Max)) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus Favorit",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}