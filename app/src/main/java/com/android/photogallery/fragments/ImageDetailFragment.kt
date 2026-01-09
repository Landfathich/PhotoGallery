package com.android.photogallery.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.photogallery.databinding.FragmentImageDetailBinding
import com.android.photogallery.models.ImageResult
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
    }
}