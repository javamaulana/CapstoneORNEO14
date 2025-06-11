package com.example.currencyconverterpro.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.db.User
import com.example.currencyconverterpro.data.db.UserDao
import com.example.currencyconverterpro.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.userIdFlow.collect { userId ->
                if (userId != null) {
                    _user.value = userDao.getUserById(userId)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}