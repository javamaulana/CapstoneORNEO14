package com.example.currencyconverterpro.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.api.RetrofitInstance
import com.example.currencyconverterpro.data.db.FavoriteDao
import com.example.currencyconverterpro.data.db.FavoritePair
import com.example.currencyconverterpro.data.preferences.SessionManager
import com.example.currencyconverterpro.ui.AppEventManager
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteDao: FavoriteDao,
    sessionManager: SessionManager
) : ViewModel() {

    // ViewModel akan mengambil daftar item favorit dari database Room, memfilternya berdasarkan 'userId' yang sedang login.
    val favorites: StateFlow<List<FavoritePair>> = sessionManager.userIdFlow.flatMapLatest { userId ->
        favoriteDao.getFavoritesByUser(userId ?: -1)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    init {
        favorites.onEach { favoriteList ->
            if (favoriteList.isNotEmpty()) {
                refreshFavorites()
            }
        }.launchIn(viewModelScope)
    }

    fun refreshFavorites() {
        viewModelScope.launch {
            try {
                val ratesDeferred = favorites.value.map { pair ->
                    async {
                        val response = RetrofitInstance.api.getLatestRates(pair.fromCurrency, pair.toCurrency)
                        if (response.isSuccessful && response.body() != null) {
                            val rate = response.body()!!.rates[pair.toCurrency]
                            "${pair.fromCurrency}-${pair.toCurrency}" to (rate ?: 0.0)
                        } else { null }
                    }
                }
                _rates.value = ratesDeferred.awaitAll().filterNotNull().toMap()
            } catch (e: Exception) {
                Log.e("FavoritesVM", "Gagal refresh rates: ${e.message}")
            }
        }
    }

    fun delete(favorite: FavoritePair) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(favorite)
            AppEventManager.showSnackbar("Item telah dihapus dari favorit.")
        }
    }
}