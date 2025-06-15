package com.example.currencyconverterpro.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import coil.compose.AsyncImage
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDetailScreen(
    currencyCode: String,
    navController: NavController,
    viewModel: CurrencyDetailViewModel,
    allCurrencies: Map<String, String>
) {
    val historicalData by viewModel.historicalData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val numberFormatter = remember { DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale("in", "ID"))) }

    val initialBaseCurrency = if (currencyCode == "USD") "EUR" else "USD"
    var baseCurrency by remember { mutableStateOf(initialBaseCurrency) }
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = currencyCode) {
        viewModel.fetchHistoricalData(currencyCode, baseCurrency, startDate, endDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        // PERBAIKAN FINAL: Ganti statusBarsPadding() dengan padding manual
                        modifier = Modifier.padding(top = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val countryCode = CurrencyDataMapper.getCountryCode(currencyCode)
                        if (countryCode != null) {
                            AsyncImage(
                                model = CurrencyDataMapper.getFlagUrl(countryCode),
                                contentDescription = "Bendera $currencyCode",
                                modifier = Modifier.size(32.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Text("Detail Kurs $currencyCode")
                    }
                },
                navigationIcon = {
                    IconButton(
                        // PERBAIKAN FINAL: Ganti statusBarsPadding() dengan padding manual
                        modifier = Modifier.padding(top = 18.dp),
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                windowInsets = WindowInsets(0.dp), // Ini tetap diperlukan untuk mematikan padding otomatis
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pengaturan Tampilan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        CurrencyDropdown(
                            label = "Basis Perbandingan",
                            currencies = allCurrencies,
                            selectedCurrency = baseCurrency,
                            onCurrencySelected = { newBase -> baseCurrency = newBase }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pilih Rentang Tanggal", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) { Text(startDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))) }
                            Text("hingga")
                            OutlinedButton(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) { Text(endDate.format(DateTimeFormatter.ofPattern("dd MMM yy"))) }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchHistoricalData(currencyCode, baseCurrency, startDate, endDate) }, modifier = Modifier.fillMaxWidth()) { Text("Tampilkan Kurs") }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
            }

            item {
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 16.dp))
                }
            }

            if (isLoading) {
                item { Box(modifier = Modifier.fillParentMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (historicalData.isNotEmpty()) {
                item {
                    Text("Grafik $currencyCode vs $baseCurrency", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
                    val pointsData = historicalData.mapIndexed { index, data -> Point(index.toFloat(), data.rate.toFloat()) }; val axisLabelColor = MaterialTheme.colorScheme.onSurface; val axisLineColor = axisLabelColor.copy(alpha = 0.6f); val xAxisData = AxisData.Builder().axisStepSize(100.dp).backgroundColor(Color.Transparent).steps(pointsData.size - 1).labelData { i -> when (i) { 0 -> historicalData.first().date.substring(5); pointsData.size / 2 -> historicalData[pointsData.size / 2].date.substring(5); pointsData.size - 1 -> historicalData.last().date.substring(5); else -> "" } }.labelAndAxisLinePadding(15.dp).axisLineColor(axisLineColor).axisLabelColor(axisLabelColor).build(); val yAxisData = AxisData.Builder().steps(5).backgroundColor(Color.Transparent).labelAndAxisLinePadding(20.dp).labelData { i -> val max = historicalData.maxOfOrNull { it.rate } ?: 0.0; val min = historicalData.minOfOrNull { it.rate } ?: 0.0; if (max == min) return@labelData numberFormatter.format(max); val value = min + (i * ((max - min) / 5)); numberFormatter.format(value) }.axisLineColor(axisLineColor).axisLabelColor(axisLabelColor).build(); val lineChartData = LineChartData(linePlotData = LinePlotData(lines = listOf(Line(dataPoints = pointsData, lineStyle = LineStyle(color = MaterialTheme.colorScheme.primary), intersectionPoint = IntersectionPoint(color = MaterialTheme.colorScheme.primary), selectionHighlightPoint = SelectionHighlightPoint(color = MaterialTheme.colorScheme.tertiary), shadowUnderLine = ShadowUnderLine(alpha = 0.5f, brush = androidx.compose.ui.graphics.Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent))), selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y -> val index = x.toInt(); if (index < historicalData.size) { val date = historicalData[index].date; val rate = numberFormatter.format(y); "$date\n Kurs: $rate" } else { "" } })))), xAxisData = xAxisData, yAxisData = yAxisData, gridLines = GridLines(color = axisLineColor.copy(alpha = 0.5f)), backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)); LineChart(modifier = Modifier.fillMaxWidth().height(300.dp), lineChartData = lineChartData)
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                }

                item {
                    Text("Rincian Data Harian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                }

                val sortedData = historicalData.sortedByDescending { it.date }
                itemsIndexed(sortedData) { index, data ->
                    val currentRate = data.rate; val previousDayRate = if (index < sortedData.size - 1) sortedData[index + 1].rate else null; val trend: Pair<ImageVector, Color>? = when { previousDayRate == null -> null; currentRate > previousDayRate -> Pair(Icons.Default.ArrowDropUp, Color(0xFF4CAF50)); currentRate < previousDayRate -> Pair(Icons.Default.ArrowDropDown, Color(0xFFF44336)); else -> null }; Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Text(data.date, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f)); Row(verticalAlignment = Alignment.CenterVertically) { if (trend != null) { Icon(imageVector = trend.first, contentDescription = "Tren", tint = trend.second, modifier = Modifier.size(24.dp)) }; Spacer(modifier = Modifier.width(4.dp)); Text(numberFormatter.format(data.rate), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold) } } }
                }
            }
        }
    }

    if (showStartDatePicker) { val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); DatePickerDialog(onDismissRequest = { showStartDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showStartDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") } }) { DatePicker(state = datePickerState) } }
    if (showEndDatePicker) { val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()); DatePickerDialog(onDismissRequest = { showEndDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }; showEndDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Batal") } }) { DatePicker(state = datePickerState) } }
}