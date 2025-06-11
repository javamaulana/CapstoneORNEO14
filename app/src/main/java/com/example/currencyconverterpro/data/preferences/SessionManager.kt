package com.example.currencyconverterpro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = intPreferencesKey("user_id")
        val LAST_FROM_CURRENCY = stringPreferencesKey("last_from_currency")
        val LAST_TO_CURRENCY = stringPreferencesKey("last_to_currency")
    }

    suspend fun saveSession(userId: Int) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[USER_ID] = userId
        }
    }

    // --- PERBAIKAN: Menyederhanakan fungsi logout ---
    // Kita hanya menghapus data sesi (login & user id), bukan data preferensi mata uang
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(USER_ID)
        }
    }

    suspend fun saveLastSelectedCurrencies(from: String, to: String) {
        dataStore.edit { preferences ->
            preferences[LAST_FROM_CURRENCY] = from
            preferences[LAST_TO_CURRENCY] = to
        }
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    val userIdFlow: Flow<Int?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val lastFromCurrencyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LAST_FROM_CURRENCY] ?: "USD"
    }

    val lastToCurrencyFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[LAST_TO_CURRENCY] ?: "IDR"
    }
}