package com.android.photogallery.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.MainActivity
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.api.RetrofitClient
import com.android.photogallery.databinding.FragmentSearchBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.extensions.favoritesRepository
import com.android.photogallery.utils.extensions.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: ImageAdapter
    private val images = mutableListOf<ImageResult>()
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeFavoriteIds()
    }

    private fun setupRecyclerView() {
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ImageAdapter(
            images = images,
            onItemClick = { image -> showImageDetail(image) },
            favoriteIds = emptySet()
        )

        binding.imagesRecyclerView.adapter = adapter

        binding.imagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 10) {
                    hideKeyboard()
                }

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && currentPage < totalPages) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        loadMoreData()
                    }
                }
            }
        })
    }

    private fun observeFavoriteIds() {
        lifecycleScope.launch {
            favoritesRepository.getFavoriteIdsStream().collectLatest { favoriteIds ->
                adapter.updateFavoriteIds(favoriteIds)
            }
        }
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                hideKeyboard()
                currentPage = 1
                searchImages(query)
            } else {
                Toast.makeText(requireContext(), "Please enter search query", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        adapter.onFavoriteClick = { image, shouldBeFavorite ->
            lifecycleScope.launch {
                if (shouldBeFavorite) {
                    favoritesRepository.addToFavorites(image)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    favoritesRepository.removeFromFavorites(image.id)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun showImageDetail(image: ImageResult) {
        (requireActivity() as MainActivity).showImageDetail(image)
    }

    private fun searchImages(query: String) {
        if (isLoading) return

        isLoading = true
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.searchImages(
                    query = query,
                    page = currentPage,
                )

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.let {
                        totalPages = it.page_count

                        if (currentPage == 1) {
                            images.clear()
                        }

                        it.results?.let { results ->
                            images.addAll(results)
                            adapter.updateImages(images)
                        }

                        Toast.makeText(
                            requireContext(),
                            currentPage.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Network error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("API_ERROR", e.toString())
            } finally {
                isLoading = false
                showLoading(false)
            }
        }
    }

    private fun loadMoreData() {
        if (isLoading) return

        currentPage++
        val query = binding.searchEditText.text.toString()
        if (query.isNotEmpty()) {
            searchImages(query)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}