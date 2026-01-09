package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.photogallery.MainActivity
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.databinding.FragmentSavedImagesBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.FavoritesManager

class SavedImagesFragment : Fragment() {

    private lateinit var binding: FragmentSavedImagesBinding
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSavedImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем при каждом возвращении на фрагмент
        loadFavorites()
    }

    private fun loadFavorites() {
        // Берем реальные избранные картинки из FavoritesManager
        val favorites = FavoritesManager.getFavorites()

        if (favorites.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.savedImagesRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.savedImagesRecyclerView.visibility = View.VISIBLE
            adapter.updateImages(favorites)
        }
    }

    private fun setupRecyclerView() {
        binding.savedImagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ImageAdapter(emptyList()) { image ->
            showImageDetail(image)
        }

        // Добавляем обработчик для кнопки избранного
        adapter.onFavoriteClick = { image, shouldBeFavorite ->
            if (!shouldBeFavorite) {
                // Если нажали на звезду (удаляем из избранного)
                FavoritesManager.removeFromFavorites(image.id)
                loadFavorites() // Перезагружаем список
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            }
            // Если shouldBeFavorite = true, игнорируем (нельзя добавить в избранном)
        }

        binding.savedImagesRecyclerView.adapter = adapter
    }

    private fun showImageDetail(image: ImageResult) {
        (requireActivity() as MainActivity).showImageDetail(image)
    }
}