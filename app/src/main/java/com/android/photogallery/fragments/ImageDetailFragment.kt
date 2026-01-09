package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.photogallery.databinding.FragmentImageDetailBinding
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.FavoritesManager
import com.bumptech.glide.Glide

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

        updateFavoriteButton()

        // Обработчик клика на кнопку избранного
        binding.favoriteButton.setOnClickListener {
            val isCurrentlyFavorite = FavoritesManager.isFavorite(image.id)

            if (isCurrentlyFavorite) {
                FavoritesManager.removeFromFavorites(image.id)
                Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FavoritesManager.addToFavorites(image)
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            }

            updateFavoriteButton()
        }
    }

    private fun updateFavoriteButton() {
        val isFavorite = FavoritesManager.isFavorite(image.id)

        val iconRes = if (isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }

        binding.favoriteButton.setImageResource(iconRes)
    }
}