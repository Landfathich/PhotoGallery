package com.android.photogallery.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.photogallery.api.RetrofitClient
import com.android.photogallery.models.ImageResult
import com.android.photogallery.repository.FavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val favoritesRepository = FavoritesRepository.getInstance()

    // 1. Заменяем переменные из фрагмента на StateFlow
    private val _images = MutableStateFlow<List<ImageResult>>(emptyList())
    val images: StateFlow<List<ImageResult>> = _images.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        viewModelScope.launch {
            favoritesRepository.getFavoriteIdsStream().collect { ids ->
                _favoriteIds.value = ids
            }
        }
    }

    // 2. Переносим метод searchImages
    fun searchImages(query: String) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.searchImages(
                    query = query,
                    page = _currentPage.value,
                )

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        _totalPages.value = it.page_count

                        if (_currentPage.value == 1) {
                            _images.value = emptyList() // Очищаем вместо images.clear()
                        }

                        it.results?.let { results ->
                            // Если первая страница - заменяем, если нет - добавляем
                            val currentImages = _images.value
                            _images.value = if (_currentPage.value == 1) {
                                results
                            } else {
                                currentImages + results
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Ошибки пока не обрабатываем, добавим позже
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 3. Переносим метод loadMoreData
    fun loadMore() {
        if (_isLoading.value) return

        _currentPage.value += 1
        // Поиск будет делать фрагмент, вызвав searchImages с новым currentPage
    }

    // 4. Переносим логику избранного
    fun toggleFavorite(image: ImageResult, shouldBeFavorite: Boolean) {
        viewModelScope.launch {
            if (shouldBeFavorite) {
                favoritesRepository.addToFavorites(image)
            } else {
                favoritesRepository.removeFromFavorites(image.id)
            }
        }
    }

    // 5. Вспомогательный метод для сброса при новом поиске
    fun resetForNewSearch() {
        _currentPage.value = 1
        _images.value = emptyList()
    }
}