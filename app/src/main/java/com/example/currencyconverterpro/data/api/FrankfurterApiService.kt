package com.example.currencyconverterpro.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Data class untuk menampung respons data historis
data class HistoricalRatesResponse(
    val amount: Double,
    val base: String,
    val start_date: String,
    val end_date: String,
    val rates: Map<String, Map<String, Double>> // Map<Tanggal, Map<KodeMataUang, Rate>>
)

interface FrankfurterApiService {
    @GET("currencies")
    suspend fun getCurrencies(): Response<Map<String, String>>

    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<LatestRatesResponse>

    // --- FUNGSI BARU UNTUK DATA HISTORIS ---
    // Menggunakan @Path untuk memasukkan periode tanggal langsung ke URL
    @GET("{period}")
    suspend fun getHistoricalRates(
        @Path("period") period: String, // e.g., "2024-05-12..2024-06-11"
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<HistoricalRatesResponse>
}