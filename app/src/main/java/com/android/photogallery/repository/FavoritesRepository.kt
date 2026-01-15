package com.android.photogallery.repository

import android.content.Context
import com.android.photogallery.data.database.AppDatabase
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.extensions.toFavoriteImage
import com.android.photogallery.utils.extensions.toImageResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).favoriteImageDao()

    suspend fun addToFavorites(image: ImageResult) {
        dao.insert(image.toFavoriteImage())
    }

    suspend fun removeFromFavorites(imageId: String) {
        dao.deleteById(imageId)
    }

    suspend fun isFavorite(imageId: String): Boolean {
        return dao.isFavorite(imageId)
    }

    fun getFavoritesStream(): Flow<List<ImageResult>> {
        return dao.getAll().map { favoriteImages ->
            favoriteImages.map { it.toImageResult() }
        }
    }

    fun getFavoriteIdsStream(): Flow<Set<String>> {
        return dao.getAll().map { favorites ->
            favorites.map { it.id }.toSet()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoritesRepository? = null

        fun getInstance(context: Context): FavoritesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoritesRepository(context).also { INSTANCE = it }
            }
        }
    }
}