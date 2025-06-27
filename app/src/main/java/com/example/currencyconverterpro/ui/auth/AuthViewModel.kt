package com.example.currencyconverterpro.ui.auth

import android.util.Patterns // <-- Tambahkan import ini
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverterpro.data.db.User
import com.example.currencyconverterpro.data.db.UserDao
import com.example.currencyconverterpro.data.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(emailInput: String, passwordInput: String) {
        viewModelScope.launch {
            val email = emailInput.trim().lowercase()
            val password = passwordInput.trim()
            val user = userDao.getUserByEmail(email)
            if (user != null && user.password == password) {
                sessionManager.saveSession(user.id)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Email atau password salah.")
            }
        }
    }

    fun register(name: String, email: String, password: String, gender: String) {
        viewModelScope.launch {
            val trimmedName = name.trim()
            val trimmedEmail = email.trim().lowercase()
            val trimmedPassword = password.trim()

            if (trimmedName.isBlank() || trimmedEmail.isBlank() || trimmedPassword.isBlank() || gender.isBlank()) {
                _authState.value = AuthState.Error("Semua bagian harus diisi.")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                _authState.value = AuthState.Error("Format email yang Anda masukkan tidak valid.")
                return@launch
            }

            if (userDao.getUserByEmail(trimmedEmail) != null) {
                _authState.value = AuthState.Error("Email sudah terdaftar.")
                return@launch
            }

            val newUser = User(name = trimmedName, email = trimmedEmail, password = trimmedPassword, gender = gender)
            userDao.registerUser(newUser)
            _authState.value = AuthState.RegistrationSuccess
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Success : AuthState()
    object RegistrationSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}