package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.photogallery.databinding.FragmentImageDetailBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.extensions.favoritesRepository
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class ImageDetailFragment : Fragment() {

    private lateinit var binding: FragmentImageDetailBinding
    private lateinit var image: ImageResult

    companion object {
        fun newInstance(image: ImageResult): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            fragment.image = image
            return fragment
        }
    }

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

        Glide.with(this)
            .load(image.url)
            .into(binding.detailImageView)

        with(binding) {
            titleTextView.text = image.title
            creatorTextView.text = "Creator: ${image.creator ?: "Unknown"}"
            licenseTextView.text = "License: ${image.license} ${image.license_version ?: ""}"
            dimensionsTextView.text = "Dimensions: ${image.width ?: "?"} x ${image.height ?: "?"}"
            sourceTextView.text = "Source: ${image.source ?: "Unknown"}"

            closeButton.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        loadFavoriteStatus()

        binding.favoriteButton.setOnClickListener {
            toggleFavoriteStatus()
        }
    }

    private fun loadFavoriteStatus() {
        lifecycleScope.launch {
            val isFavorite = favoritesRepository.isFavorite(image.id)
            updateFavoriteButton(isFavorite)
        }
    }

    private fun toggleFavoriteStatus() {
        lifecycleScope.launch {
            val isCurrentlyFavorite = favoritesRepository.isFavorite(image.id)

            if (isCurrentlyFavorite) {
                favoritesRepository.removeFromFavorites(image.id)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
                updateFavoriteButton(false)
            } else {
                favoritesRepository.addToFavorites(image)
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
                updateFavoriteButton(true)
            }
        }
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