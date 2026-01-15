package com.android.photogallery.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.photogallery.data.entities.FavoriteImage
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteImage: FavoriteImage)

    @Delete
    suspend fun delete(favoriteImage: FavoriteImage)

    @Query("DELETE FROM favorite_images WHERE id = :imageId")
    suspend fun deleteById(imageId: String)

    @Query("SELECT * FROM favorite_images ORDER BY id")
    fun getAll(): Flow<List<FavoriteImage>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_images WHERE id = :imageId)")
    suspend fun isFavorite(imageId: String): Boolean
}