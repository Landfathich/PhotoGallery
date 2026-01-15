package com.android.photogallery.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_images")
data class FavoriteImage(
    @PrimaryKey
    val id: String,
    val title: String?,
    val url: String,
    val thumbnail: String?,
    val creator: String?,
    val license: String,
    val width: Int?,
    val height: Int?,
    val source: String?
)