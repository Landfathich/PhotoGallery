package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.android.photogallery.adapters.ImageAdapter
import com.android.photogallery.MainActivity
import com.android.photogallery.databinding.FragmentSavedImagesBinding
import com.android.photogallery.models.ImageResult

class SavedImagesFragment : Fragment() {

    private lateinit var binding: FragmentSavedImagesBinding;

    private lateinit var adapter: ImageAdapter
    private val savedImages = mutableListOf<ImageResult>()

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

        if (savedImages.isEmpty()) {
            savedImages.addAll(createTestImages())
        }

        setupRecyclerView()
        updateEmptyState()
    }

    private fun createTestImages(): List<ImageResult> {
        return listOf(
            ImageResult(
                id = "1",
                title = "Test Image 1",
                url = "https://karton33.ru/images/kristall/korob.png",
                thumbnail = null,
                creator = "Test Creator",
                creator_url = null,
                license = "CC0",
                license_version = "1.0",
                license_url = null,
                provider = "Test",
                source = "Test Source",
                tags = null,
                attribution = null,
                height = 500,
                width = 500
            )
        )
    }

    private fun setupRecyclerView() {
        binding.savedImagesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ImageAdapter(savedImages) { image ->
            showImageDetail(image)
        }
        binding.savedImagesRecyclerView.adapter = adapter
    }

    private fun showImageDetail(image: ImageResult) {
        (requireActivity() as MainActivity).showImageDetail(image)
    }

    private fun updateEmptyState() {
        if (savedImages.isEmpty()) {
            binding.emptyStateText.visibility = View.VISIBLE
            binding.savedImagesRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateText.visibility = View.GONE
            binding.savedImagesRecyclerView.visibility = View.VISIBLE
        }
    }
}