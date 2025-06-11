package com.example.currencyconverterpro.ui.main

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel,
    fromArg: String?,
    toArg: String?
) {
    val currencies by viewModel.currencies.collectAsState()
    val conversionResultData by viewModel.conversionResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val fromCurrency by viewModel.fromCurrency.collectAsState()
    val toCurrency by viewModel.toCurrency.collectAsState()
    val amount by viewModel.amount.collectAsState()

    val largeNumberFormatter = remember { DecimalFormat("#,##0.0", DecimalFormatSymbols(Locale("in", "ID"))) }
    val smallNumberFormatter = remember { DecimalFormat("#,##0.00000", DecimalFormatSymbols(Locale("in", "ID"))) }

    val animatedAmount by animateFloatAsState(
        targetValue = (conversionResultData?.totalAmount ?: 0.0).toFloat(),
        animationSpec = tween(durationMillis = 800), label = "amountAnimation"
    )

    LaunchedEffect(key1 = fromArg, key2 = toArg) {
        if (fromArg != null && toArg != null) {
            viewModel.updateFromFavorite(fromArg, toArg)
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Konversi Mata Uang", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.onAmountChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.headlineSmall,
                        visualTransformation = NumberFormattingVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyDropdown(
                        label = "Dari",
                        currencies = currencies,
                        selectedCurrency = fromCurrency,
                        onCurrencySelected = { viewModel.onFromCurrencyChange(it) }
                    )
                }
            }

            Box(modifier = Modifier.padding(vertical = 8.dp)) {
                FilledIconButton(
                    onClick = { viewModel.onSwapCurrencies() },
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.SwapVert, contentDescription = "Tukar Mata Uang")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CurrencyDropdown(
                        label = "Ke",
                        currencies = currencies,
                        selectedCurrency = toCurrency,
                        onCurrencySelected = { viewModel.onToCurrencyChange(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading && conversionResultData == null) {
                        CircularProgressIndicator(modifier = Modifier.padding(vertical = 48.dp))
                    } else {
                        val formattedTotalAmount = largeNumberFormatter.format(animatedAmount)
                        val adaptiveFontSize = when {
                            formattedTotalAmount.length > 15 -> 32.sp
                            formattedTotalAmount.length > 10 -> 40.sp
                            else -> MaterialTheme.typography.displayMedium.fontSize
                        }

                        Text(
                            text = formattedTotalAmount,
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = adaptiveFontSize),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = toCurrency,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AnimatedVisibility(visible = conversionResultData != null) {
                            conversionResultData?.let { result ->
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider()
                                Box(modifier = Modifier.padding(top = 24.dp)) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "1 $fromCurrency = ${largeNumberFormatter.format(result.unitRate)} $toCurrency",
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "1 $toCurrency = ${smallNumberFormatter.format(result.inverseUnitRate)} $fromCurrency",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        TextButton(onClick = { viewModel.addFavorite() }) {
            Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorit", modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tambahkan ke Favorit")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.convert() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("KONVERSI", fontSize = 16.sp)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    currencies: Map<String, String>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                val countryCode = CurrencyDataMapper.getCountryCode(selectedCurrency)
                if (countryCode != null) {
                    AsyncImage(
                        model = CurrencyDataMapper.getFlagUrl(countryCode),
                        contentDescription = "Bendera $selectedCurrency",
                        modifier = Modifier.size(24.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            currencies.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val countryCode = CurrencyDataMapper.getCountryCode(code)
                            if (countryCode != null) {
                                AsyncImage(
                                    model = CurrencyDataMapper.getFlagUrl(countryCode),
                                    contentDescription = "Bendera $name",
                                    modifier = Modifier.size(24.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                            Text("$code - $name")
                        }
                    },
                    onClick = {
                        onCurrencySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}