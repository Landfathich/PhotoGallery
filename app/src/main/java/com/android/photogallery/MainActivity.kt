package com.android.photogallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.photogallery.databinding.ActivityMainBinding
import com.android.photogallery.models.ImageResult

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        if (savedInstanceState == null) {
            showSearchFragment()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    showSearchFragment()
                    true
                }

                R.id.navigation_saved -> {
                    showSavedFragment()
                    true
                }

                else -> false
            }
        }
    }

    fun showImageDetail(image: ImageResult) {
        val fragment = ImageDetailFragment.newInstance(image)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_up,
                R.anim.fragment_slide_down,
                R.anim.fragment_slide_up,
                R.anim.fragment_slide_down
            )
            .replace(R.id.main_content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content_container, SearchFragment())
            .commit()
    }

    private fun showSavedFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content_container, SavedImagesFragment())
            .commit()
    }
}