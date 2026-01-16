package com.android.photogallery.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.photogallery.models.ImageResult
import com.android.photogallery.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageDetailViewModel : ViewModel() {
    private val favoritesRepository = FavoritesRepository.getInstance()

    // StateFlow для статуса избранного
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // ID текущего изображения
    private var currentImageId: String? = null

    /**
     * Загружает и подписывается на статус избранного для изображения
     */
    fun loadFavoriteStatus(imageId: String) {
        currentImageId = imageId

        viewModelScope.launch {
            // Первоначальная проверка
            val initialStatus = favoritesRepository.isFavorite(imageId)
            _isFavorite.value = initialStatus

            // Подписываемся на изменения всех избранных
            // Если текущее изображение добавили/удалили - обновим статус
            favoritesRepository.getFavoriteIdsStream().collect { favoriteIds ->
                _isFavorite.value = favoriteIds.contains(imageId)
            }
        }
    }

    /**
     * Переключает статус избранного для текущего изображения
     */
    fun toggleFavorite(image: ImageResult) {
        viewModelScope.launch {
            val currentStatus = _isFavorite.value

            if (currentStatus) {
                // Удаляем из избранного
                favoritesRepository.removeFromFavorites(image.id)
            } else {
                // Добавляем в избранное
                favoritesRepository.addToFavorites(image)
            }

            // StateFlow автоматически обновится через подписку в loadFavoriteStatus
        }
    }
}