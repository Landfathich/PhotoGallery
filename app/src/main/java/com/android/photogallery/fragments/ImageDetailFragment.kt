package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.photogallery.databinding.FragmentImageDetailBinding
import com.android.photogallery.viewmodels.ImageDetailViewModel
import com.android.photogallery.viewmodels.SharedViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ImageDetailFragment : Fragment() {

    private lateinit var binding: FragmentImageDetailBinding

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val imageDetailViewModel: ImageDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Подписываемся на текущее изображение из SharedViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.currentImage.collect { image ->
                if (image != null) {
                    // Показываем изображение
                    displayImage(image)
                    // Загружаем статус избранного для этого изображения
                    imageDetailViewModel.loadFavoriteStatus(image.id)
                } else {
                    // Если изображения нет (например, при восстановлении и SharedViewModel пуста)
                    parentFragmentManager.popBackStack()
                }
            }
        }

        // Подписываемся на статус избранного из ImageDetailViewModel
        observeFavoriteStatus()

        // Обработка кликов
        binding.closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.favoriteButton.setOnClickListener {
            toggleFavoriteStatus()
        }
    }

    private fun displayImage(image: com.android.photogallery.models.ImageResult) {
        // Загружаем изображение
        Glide.with(this)
            .load(image.url)
            .into(binding.detailImageView)

        // Заполняем информацию
        with(binding) {
            titleTextView.text = image.title
            creatorTextView.text = "Creator: ${image.creator ?: "Unknown"}"
            licenseTextView.text = "License: ${image.license} ${image.license_version ?: ""}"
            dimensionsTextView.text = "Dimensions: ${image.width ?: "?"} x ${image.height ?: "?"}"
            sourceTextView.text = "Source: ${image.source ?: "Unknown"}"
        }
    }

    private fun observeFavoriteStatus() {
        // Наблюдаем за статусом избранного
        viewLifecycleOwner.lifecycleScope.launch {
            imageDetailViewModel.isFavorite.collect { isFavorite ->
                updateFavoriteButton(isFavorite)
            }
        }
    }

    private fun toggleFavoriteStatus() {
        // Берем текущее изображение из SharedViewModel
        val image = sharedViewModel.currentImage.value ?: return

        // Получаем текущий статус избранного
        val currentStatus = imageDetailViewModel.isFavorite.value

        // Переключаем статус через ViewModel
        imageDetailViewModel.toggleFavorite(image)

        // Показываем Toast
        val message = if (currentStatus) {
            "Removed from favorites"
        } else {
            "Added to favorites"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val iconRes = if (isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        binding.favoriteButton.setImageResource(iconRes)
    }
}