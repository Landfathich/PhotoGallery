package com.android.photogallery

import android.app.Application
import com.android.photogallery.repository.FavoritesRepository

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FavoritesRepository.init(this)
    }
}