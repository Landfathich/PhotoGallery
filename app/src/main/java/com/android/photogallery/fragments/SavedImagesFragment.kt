package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.android.photogallery.MainActivity
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.databinding.FragmentSavedImagesBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.extensions.favoritesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        observeFavorites()
    }

    private fun setupRecyclerView() {
        binding.savedImagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ImageAdapter(
            images = emptyList(),
            onItemClick = { image -> showImageDetail(image) },
            favoriteIds = emptySet()
        )

        adapter.onFavoriteClick = { image, shouldBeFavorite ->
            lifecycleScope.launch {
                if (!shouldBeFavorite) {
                    favoritesRepository.removeFromFavorites(image.id)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.savedImagesRecyclerView.adapter = adapter
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            favoritesRepository.getFavoritesStream().collectLatest { favorites ->
                if (favorites.isEmpty()) {
                    showEmptyState()
                } else {
                    showFavoritesList(favorites)
                }
            }
        }
    }

    private fun showFavoritesList(favorites: List<ImageResult>) {
        binding.emptyStateText.visibility = View.GONE
        binding.savedImagesRecyclerView.visibility = View.VISIBLE

        val favoriteIds = favorites.map { it.id }.toSet()
        adapter.updateImages(favorites)
        adapter.updateFavoriteIds(favoriteIds)
    }

    private fun showEmptyState() {
        binding.emptyStateText.visibility = View.VISIBLE
        binding.savedImagesRecyclerView.visibility = View.GONE
    }

    private fun showImageDetail(image: ImageResult) {
        (requireActivity() as MainActivity).showImageDetail(image)
    }
}