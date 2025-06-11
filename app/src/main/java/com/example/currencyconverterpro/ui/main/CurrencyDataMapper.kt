package com.example.currencyconverterpro.ui.main

object CurrencyDataMapper {
    // Peta dari kode mata uang ke kode negara (dua huruf, huruf kecil)
    private val currencyToCountryCodeMap = mapOf(
        "AUD" to "au", "BGN" to "bg", "BRL" to "br", "CAD" to "ca",
        "CHF" to "ch", "CNY" to "cn", "CZK" to "cz", "DKK" to "dk",
        "EUR" to "eu", "GBP" to "gb", "HKD" to "hk", "HUF" to "hu",
        "IDR" to "id", "ILS" to "il", "INR" to "in", "ISK" to "is",
        "JPY" to "jp", "KRW" to "kr", "MXN" to "mx", "MYR" to "my",
        "NOK" to "no", "NZD" to "nz", "PHP" to "ph", "PLN" to "pl",
        "RON" to "ro", "SEK" to "se", "SGD" to "sg", "THB" to "th",
        "TRY" to "tr", "USD" to "us", "ZAR" to "za"
    )

    fun getCountryCode(currencyCode: String): String? {
        return currencyToCountryCodeMap[currencyCode]
    }

    // Fungsi untuk mendapatkan URL bendera dari flagcdn.com
    fun getFlagUrl(countryCode: String): String {
        return "https://flagcdn.com/w80/${countryCode.lowercase()}.png"
    }
}