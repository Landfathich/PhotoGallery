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
import androidx.recyclerview.widget.GridLayoutManager
import com.android.photogallery.MainActivity
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.databinding.FragmentSavedImagesBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.viewmodels.SavedImagesViewModel
import com.android.photogallery.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

class SavedImagesFragment : Fragment() {

    private lateinit var binding: FragmentSavedImagesBinding
    private lateinit var adapter: ImageAdapter

    // Добавляем ViewModel
    private val viewModel: SavedImagesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.savedImagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ImageAdapter(
            images = emptyList(),
            onItemClick = { image ->
                sharedViewModel.setCurrentImage(image)
                (requireActivity() as MainActivity).showImageDetail()
            },
            favoriteIds = emptySet()
        )

        adapter.onFavoriteClick = { image, shouldBeFavorite ->
            // Вызываем метод ViewModel вместо прямого вызова репозитория
            if (!shouldBeFavorite) {
                viewModel.removeFromFavorites(image.id)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.savedImagesRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        // Наблюдаем за списком избранных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { favorites ->
                if (favorites.isEmpty()) {
                    showEmptyState()
                } else {
                    showFavoritesList(favorites)
                }
            }
        }

        // Наблюдаем за ID избранных (для обновления адаптера)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteIds.collect { favoriteIds ->
                adapter.updateFavoriteIds(favoriteIds)
            }
        }
    }

    private fun showFavoritesList(favorites: List<ImageResult>) {
        binding.emptyStateText.visibility = View.GONE
        binding.savedImagesRecyclerView.visibility = View.VISIBLE
        adapter.updateImages(favorites)
    }

    private fun showEmptyState() {
        binding.emptyStateText.visibility = View.VISIBLE
        binding.savedImagesRecyclerView.visibility = View.GONE
    }
}