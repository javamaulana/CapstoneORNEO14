package com.example.currencyconverterpro.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconverterpro.data.db.AppDatabase
import com.example.currencyconverterpro.data.preferences.SessionManager
import com.example.currencyconverterpro.ui.auth.AuthViewModel
import com.example.currencyconverterpro.ui.main.ConverterViewModel
import com.example.currencyconverterpro.ui.main.CurrencyDetailViewModel
import com.example.currencyconverterpro.ui.main.FavoritesViewModel
import com.example.currencyconverterpro.ui.main.ProfileViewModel
import com.example.currencyconverterpro.ui.splash.SplashViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(context)
        val sessionManager = SessionManager(context)

        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(db.userDao(), sessionManager) as T
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel(sessionManager) as T
            modelClass.isAssignableFrom(ConverterViewModel::class.java) -> ConverterViewModel(db.favoriteDao(), sessionManager) as T
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> FavoritesViewModel(db.favoriteDao(), sessionManager) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(db.userDao(), sessionManager) as T
            modelClass.isAssignableFrom(CurrencyDetailViewModel::class.java) -> CurrencyDetailViewModel() as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}