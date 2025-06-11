package com.example.currencyconverterpro.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.api.RetrofitInstance
import com.example.currencyconverterpro.data.db.FavoriteDao
import com.example.currencyconverterpro.data.db.FavoritePair
import com.example.currencyconverterpro.data.preferences.SessionManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteDao: FavoriteDao,
    sessionManager: SessionManager
) : ViewModel() {

    val favorites: StateFlow<List<FavoritePair>> = sessionManager.userIdFlow.flatMapLatest { userId ->
        favoriteDao.getFavoritesByUser(userId ?: -1)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // State untuk menyimpan kurs terkini dari setiap pasangan favorit
    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    // State untuk pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        // Saat daftar favorit berubah (misal: setelah menambah/menghapus),
        // otomatis muat ulang kursnya.
        favorites.onEach { favoriteList ->
            if (favoriteList.isNotEmpty()) {
                refreshFavorites()
            }
        }.launchIn(viewModelScope)
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // Ambil semua kurs secara bersamaan (concurrently) untuk efisiensi
                val ratesDeferred = favorites.value.map { pair ->
                    async {
                        val response = RetrofitInstance.api.getLatestRates(pair.fromCurrency, pair.toCurrency)
                        if (response.isSuccessful && response.body() != null) {
                            val rate = response.body()!!.rates[pair.toCurrency]
                            // Buat kunci unik untuk setiap pasangan, misal: "USD-IDR"
                            "${pair.fromCurrency}-${pair.toCurrency}" to (rate ?: 0.0)
                        } else {
                            null
                        }
                    }
                }
                // Update state dengan semua hasil yang berhasil didapat
                _rates.value = ratesDeferred.awaitAll().filterNotNull().toMap()
            } catch (e: Exception) {
                Log.e("FavoritesVM", "Gagal refresh rates: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun delete(favorite: FavoritePair) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(favorite)
        }
    }
}