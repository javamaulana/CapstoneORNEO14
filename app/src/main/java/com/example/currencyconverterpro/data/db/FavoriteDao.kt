package com.example.currencyconverterpro.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoritePair: FavoritePair)

    @Delete
    suspend fun deleteFavorite(favorite: FavoritePair)

    @Query("SELECT * FROM favorite_pair_table WHERE userId = :userId ORDER BY fromCurrency ASC")
    fun getFavoritesByUser(userId: Int): Flow<List<FavoritePair>>

    @Query("SELECT * FROM favorite_pair_table WHERE userId = :userId AND ((fromCurrency = :from AND toCurrency = :to) OR (fromCurrency = :to AND toCurrency = :from)) LIMIT 1")
    suspend fun isFavoriteExist(userId: Int, from: String, to: String): FavoritePair?
}