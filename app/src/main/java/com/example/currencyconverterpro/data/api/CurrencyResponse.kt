package com.example.currencyconverterpro.data.api

data class LatestRatesResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)