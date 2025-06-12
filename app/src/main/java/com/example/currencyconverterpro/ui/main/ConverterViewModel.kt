package com.example.currencyconverterpro.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.api.RetrofitInstance
import com.example.currencyconverterpro.data.db.FavoriteDao
import com.example.currencyconverterpro.data.db.FavoritePair
import com.example.currencyconverterpro.data.preferences.SessionManager
import com.example.currencyconverterpro.ui.AppEventManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ConversionResultData(
    val totalAmount: Double,
    val unitRate: Double,
    val inverseUnitRate: Double
)

class ConverterViewModel(
    private val favoriteDao: FavoriteDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _currencies = MutableStateFlow<Map<String, String>>(emptyMap())
    val currencies: StateFlow<Map<String, String>> = _currencies.asStateFlow()

    private val _conversionResult = MutableStateFlow<ConversionResultData?>(null)
    val conversionResult: StateFlow<ConversionResultData?> = _conversionResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentUserId: Int? = null

    private val _fromCurrency = MutableStateFlow("USD")
    val fromCurrency: StateFlow<String> = _fromCurrency.asStateFlow()

    private val _toCurrency = MutableStateFlow("IDR")
    val toCurrency: StateFlow<String> = _toCurrency.asStateFlow()

    private val _amount = MutableStateFlow("1000")
    val amount: StateFlow<String> = _amount.asStateFlow()

    init {
        viewModelScope.launch {
            _fromCurrency.value = sessionManager.lastFromCurrencyFlow.first()
            _toCurrency.value = sessionManager.lastToCurrencyFlow.first()
            sessionManager.userIdFlow.collect { userId ->
                currentUserId = userId
            }
        }
        fetchCurrencies()
    }

    private fun saveLastUsedCurrencies() {
        viewModelScope.launch {
            sessionManager.saveLastSelectedCurrencies(_fromCurrency.value, _toCurrency.value)
        }
    }

    fun onFromCurrencyChange(newFrom: String) {
        if (newFrom == _toCurrency.value) {
            _toCurrency.value = if (newFrom == "USD") "IDR" else "USD"
        }
        _fromCurrency.value = newFrom
        saveLastUsedCurrencies()
    }

    fun onToCurrencyChange(newTo: String) {
        if (newTo == _fromCurrency.value) {
            _fromCurrency.value = if (newTo == "USD") "IDR" else "USD"
        }
        _toCurrency.value = newTo
        saveLastUsedCurrencies()
    }

    fun onAmountChange(newAmount: String) {
        if (newAmount.all { it.isDigit() } && newAmount.length <= 15) {
            _amount.value = newAmount.ifEmpty { "0" }
        }
    }

    fun onSwapCurrencies() {
        val temp = _fromCurrency.value
        _fromCurrency.value = _toCurrency.value
        _toCurrency.value = temp
        saveLastUsedCurrencies()
    }

    fun updateFromFavorite(from: String, to: String) {
        _fromCurrency.value = from
        _toCurrency.value = to
        saveLastUsedCurrencies()
        convert()
    }

    private fun fetchCurrencies() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getCurrencies()
                if (response.isSuccessful) {
                    _currencies.value = response.body() ?: emptyMap()
                } else {
                    AppEventManager.showSnackbar("Gagal memuat daftar mata uang.")
                }
            } catch (e: Exception) {
                AppEventManager.showSnackbar("Gagal memuat daftar mata uang.")
                Log.e("ConverterVM", "fetchCurrencies error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun convert() {
        val from = _fromCurrency.value
        val to = _toCurrency.value
        val amountStr = _amount.value

        if (from.isBlank() || to.isBlank()) {
            viewModelScope.launch { AppEventManager.showSnackbar("Pilih mata uang 'Dari' dan 'Ke'.") }
            return
        }
        val amountDouble = amountStr.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            viewModelScope.launch { AppEventManager.showSnackbar("Jumlah harus angka dan lebih dari 0.") }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getLatestRates(from, to)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val unitRate = body.rates[to]
                        if (unitRate != null) {
                            val totalAmount = unitRate * amountDouble
                            val inverseRate = 1.0 / unitRate
                            _conversionResult.value = ConversionResultData(
                                totalAmount = totalAmount,
                                unitRate = unitRate,
                                inverseUnitRate = inverseRate
                            )
                        } else { AppEventManager.showSnackbar("Gagal mendapatkan rate untuk $to.") }
                    } ?: run { AppEventManager.showSnackbar("Respons dari server kosong.") }
                } else { AppEventManager.showSnackbar("Gagal: Error ${response.code()}") }
            } catch (e: Exception) {
                AppEventManager.showSnackbar("Error: Periksa koneksi internet.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavorite() {
        viewModelScope.launch {
            val from = _fromCurrency.value
            val to = _toCurrency.value
            val userId = currentUserId
            if (userId != null && from.isNotBlank() && to.isNotBlank()) {
                val existingFavorite = favoriteDao.isFavoriteExist(userId, from, to)
                if (existingFavorite == null) {
                    favoriteDao.addFavorite(FavoritePair(userId = userId, fromCurrency = from, toCurrency = to))
                    AppEventManager.showSnackbar("Berhasil ditambahkan ke favorit!")
                } else {
                    AppEventManager.showSnackbar("Pasangan mata uang ini sudah ada di favorit.")
                }
            }
        }
    }
}