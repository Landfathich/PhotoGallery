package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.MainActivity
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.databinding.FragmentSearchBinding
import com.android.photogallery.utils.extensions.hideKeyboard
import com.android.photogallery.viewmodels.SearchViewModel
import com.android.photogallery.viewmodels.SharedViewModel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: ImageAdapter
    private val viewModel: SearchViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // Создаем адаптер с пустым списком, данные придут из ViewModel
        adapter = ImageAdapter(
            images = emptyList(),
            onItemClick = { image ->
                sharedViewModel.setCurrentImage(image)
                (requireActivity() as MainActivity).showImageDetail()
            },
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

                // Используем данные из ViewModel
                if (!viewModel.isLoading.value && viewModel.currentPage.value < viewModel.totalPages.value) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        // Вызываем метод ViewModel для загрузки следующей страницы
                        viewModel.loadMore()

                        // После увеличения страницы нужно загрузить данные
                        // с тем же запросом, если он есть
                        val query = binding.searchEditText.text.toString()
                        if (query.isNotEmpty()) {
                            viewModel.searchImages(query)
                        }
                    }
                }
            }
        })
    }

    private fun observeViewModel() {
        // Наблюдаем за списком изображений
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.images.collect { images ->
                adapter.updateImages(images)
            }
        }

        // Наблюдаем за состоянием загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                showLoading(isLoading)
            }
        }

        // Наблюдаем за избранными ID
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteIds.collect { favoriteIds ->
                adapter.updateFavoriteIds(favoriteIds)
            }
        }
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                hideKeyboard()

                // Сбрасываем состояние для нового поиска
                viewModel.resetForNewSearch()

                // Вызываем поиск через ViewModel
                viewModel.searchImages(query)
            } else {
                Toast.makeText(requireContext(), "Please enter search query", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Обработка клика на избранное
        adapter.onFavoriteClick = { image, shouldBeFavorite ->
            viewModel.toggleFavorite(image, shouldBeFavorite)

            // Показываем Toast
            val message = if (shouldBeFavorite)
                "Added to favorites"
            else
                "Removed from favorites"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}