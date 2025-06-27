package com.example.currencyconverterpro.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Objek singleton untuk menangani event yang bersifat global, seperti menampilkan Snackbar.
 */
object AppEventManager {
    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    suspend fun showSnackbar(message: String) {
        _snackbarMessages.emit(message)
    }
}