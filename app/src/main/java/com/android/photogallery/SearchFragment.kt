package com.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.api.RetrofitClient
import com.android.photogallery.databinding.FragmentSearchBinding
import com.android.photogallery.models.ImageResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var adapter: ImageAdapter
    private val images = mutableListOf<ImageResult>()
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?,
    ): android.view.View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.imagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ImageAdapter(images) { image ->
            showImageDetail(image)
        }
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
    }

    private fun showImageDetail(image: ImageResult) {
        (requireActivity() as MainActivity).showImageDetail(image)
    }

    private fun searchImages(query: String) {
        if (isLoading) return

        isLoading = true
        showLoading(true)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.searchImages(
                    query = query,
                    page = currentPage,
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        apiResponse?.let {
                            totalPages = it.page_count

                            if (currentPage == 1) {
                                images.clear()
                            }

                            it.results?.let { results ->
                                images.addAll(results)
                                adapter.notifyDataSetChanged()
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
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_ERROR", e.toString())
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    showLoading(false)
                }
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

    companion object {
        fun newInstance(): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            return fragment
        }
    }
}