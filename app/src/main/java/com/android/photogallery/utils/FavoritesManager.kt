package com.android.photogallery.utils

import com.android.photogallery.models.ImageResult

object FavoritesManager {
    // Используем Set вместо List, чтобы избежать дубликатов
    private val favorites = mutableSetOf<ImageResult>()

    fun addToFavorites(image: ImageResult) {
        favorites.add(image) // Set сам проверит, нет ли уже такой картинки
    }

    fun removeFromFavorites(imageId: String) {
        // Удаляем все картинки, у которых ID совпадает с переданным
        favorites.removeIf { currentImage ->
            currentImage.id == imageId
        }
    }

    fun isFavorite(imageId: String): Boolean {
        // Проверяем, есть ли хоть одна картинка с таким ID
        return favorites.any { currentImage ->
            currentImage.id == imageId
        }
    }

    fun getFavorites(): List<ImageResult> {
        // Преобразуем Set в List для удобства отображения
        return favorites.toList()
    }
}