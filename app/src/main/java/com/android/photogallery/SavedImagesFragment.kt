package com.android.photogallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.photogallery.databinding.FragmentSavedImagesBinding

class SavedImagesFragment : Fragment() {

    private lateinit var binding: FragmentSavedImagesBinding

    companion object {
        fun newInstance(): ImageDetailFragment {
            val fragment = ImageDetailFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSavedImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

}