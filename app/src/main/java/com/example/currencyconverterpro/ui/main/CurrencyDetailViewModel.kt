package com.example.currencyconverterpro.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class HistoricalRate(
    val date: String,
    val rate: Double
)

class CurrencyDetailViewModel : ViewModel() {
    private val _historicalData = MutableStateFlow<List<HistoricalRate>>(emptyList())
    val historicalData: StateFlow<List<HistoricalRate>> = _historicalData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchHistoricalData(targetCurrency: String, baseCurrency: String, startDate: LocalDate, endDate: LocalDate) {
        if (startDate.isAfter(endDate)) {
            viewModelScope.launch { _error.value = "Tanggal mulai tidak boleh setelah tanggal akhir." }
            return
        }

        if (targetCurrency == baseCurrency) {
            viewModelScope.launch { _error.value = "Mata uang target dan basis tidak boleh sama." }
            return
        }
        _error.value = null

        viewModelScope.launch {
            _isLoading.value = true
            _historicalData.value = emptyList()
            try {
                val period = "${startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}..${endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
                val response = RetrofitInstance.api.getHistoricalRates(
                    period = period, from = baseCurrency, to = targetCurrency
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        val sortedRates = it.rates.entries.map { entry ->
                            HistoricalRate(date = entry.key, rate = entry.value[targetCurrency] ?: 0.0)
                        }.sortedBy { rate -> rate.date }
                        _historicalData.value = sortedRates
                    }
                } else {
                    _error.value = "Gagal mengambil data (Error: ${response.code()})"
                }
            } catch (e: Exception) {
                _error.value = "Error Jaringan: Periksa koneksi internet Anda."
                Log.e("DetailVM", "Gagal mengambil data historis: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}