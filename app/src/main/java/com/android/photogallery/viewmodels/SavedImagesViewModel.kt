package com.android.photogallery.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.photogallery.models.ImageResult
import com.android.photogallery.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedImagesViewModel : ViewModel() {
    // Получаем репозиторий (синглтон)
    private val favoritesRepository = FavoritesRepository.getInstance()

    // StateFlow для списка избранных
    private val _favorites = MutableStateFlow<List<ImageResult>>(emptyList())
    val favorites: StateFlow<List<ImageResult>> = _favorites.asStateFlow()

    // StateFlow для ID избранных (для адаптера)
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        // При создании ViewModel подписываемся на изменения избранных
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            // Подписываемся на Flow из репозитория
            favoritesRepository.getFavoritesStream().collect { favoritesList ->
                _favorites.value = favoritesList
                _favoriteIds.value = favoritesList.map { it.id }.toSet()
            }
        }
    }

    fun removeFromFavorites(imageId: String) {
        viewModelScope.launch {
            favoritesRepository.removeFromFavorites(imageId)
        }
    }
}