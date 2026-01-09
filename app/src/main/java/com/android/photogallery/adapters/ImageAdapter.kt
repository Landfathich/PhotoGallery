package com.android.photogallery.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.R
import com.android.photogallery.databinding.ItemImageBinding
import com.android.photogallery.hideKeyboard
import com.android.photogallery.models.ImageResult
import com.bumptech.glide.Glide

class ImageAdapter(
    private val images: List<ImageResult>,
    private val onItemClick: (ImageResult) -> Unit,
) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemImageBinding.bind(view)

        val imageView: ImageView = binding.imageView
        val titleView: TextView = binding.titleTextView
        val licenseView: TextView = binding.licenseTextView
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

        holder.itemView.setOnClickListener {
            it.hideKeyboard()
            onItemClick(image)
        }
    }

    override fun getItemCount() = images.size
}