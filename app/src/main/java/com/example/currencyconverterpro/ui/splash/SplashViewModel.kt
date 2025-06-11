package com.example.currencyconverterpro.ui.splash

import androidx.lifecycle.ViewModel
import com.example.currencyconverterpro.data.preferences.SessionManager
import kotlinx.coroutines.flow.Flow

class SplashViewModel(sessionManager: SessionManager) : ViewModel() {
    val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedInFlow
}