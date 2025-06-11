package com.example.currencyconverterpro.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_pair_table",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["userId"])]
)
data class FavoritePair(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val fromCurrency: String,
    val toCurrency: String
)