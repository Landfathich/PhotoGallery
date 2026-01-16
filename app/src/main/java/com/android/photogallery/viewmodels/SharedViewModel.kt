// SharedViewModel.kt
package com.android.photogallery.viewmodels

import androidx.lifecycle.ViewModel
import com.android.photogallery.models.ImageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    // Текущее изображение для детального просмотра
    private val _currentImage = MutableStateFlow<ImageResult?>(null)
    val currentImage: StateFlow<ImageResult?> = _currentImage.asStateFlow()

    fun setCurrentImage(image: ImageResult) {
        _currentImage.value = image
    }

    fun clearCurrentImage() {
        _currentImage.value = null
    }
}