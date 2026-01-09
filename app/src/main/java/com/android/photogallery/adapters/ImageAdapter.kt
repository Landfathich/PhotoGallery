package com.android.photogallery.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.R
import com.android.photogallery.databinding.ItemImageBinding
import com.android.photogallery.hideKeyboard
import com.android.photogallery.models.ImageResult
import com.android.photogallery.utils.FavoritesManager
import com.bumptech.glide.Glide

class ImageAdapter(
    private var images: List<ImageResult>,
    private val onItemClick: (ImageResult) -> Unit,
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var onFavoriteClick: ((ImageResult, Boolean) -> Unit)? = null

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemImageBinding.bind(view)
        val imageView = binding.imageView
        val titleView = binding.titleTextView
        val licenseView = binding.licenseTextView
        val favoriteButton = binding.favoriteButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]

        Glide.with(holder.itemView.context)
            .load(image.url)
            .into(holder.imageView)

        holder.titleView.text = image.title
        holder.licenseView.text = "License: ${image.license}"

        updateFavoriteButton(holder.favoriteButton, image.id)

        holder.itemView.setOnClickListener {
            it.hideKeyboard()
            onItemClick(image)
        }

        holder.favoriteButton.setOnClickListener {
            val isCurrentlyFavorite = FavoritesManager.isFavorite(image.id)
            onFavoriteClick?.invoke(image, !isCurrentlyFavorite)
            updateFavoriteButton(holder.favoriteButton, image.id)
        }
    }

    fun updateImages(newImages: List<ImageResult>) {
        images = newImages
        notifyDataSetChanged()
    }

    private fun updateFavoriteButton(button: ImageButton, imageId: String) {
        val isFavorite = FavoritesManager.isFavorite(imageId)
        val iconRes = if (isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        button.setImageResource(iconRes)
    }

    override fun getItemCount() = images.size
}