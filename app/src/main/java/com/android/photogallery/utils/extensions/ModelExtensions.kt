package com.android.photogallery.utils.extensions

import com.android.photogallery.data.entities.FavoriteImage
import com.android.photogallery.models.ImageResult

fun ImageResult.toFavoriteImage(): FavoriteImage = FavoriteImage(
    id = id,
    title = title,
    url = url,
    thumbnail = thumbnail,
    creator = creator,
    license = license,
    width = width,
    height = height,
    source = source
)

fun FavoriteImage.toImageResult(): ImageResult = ImageResult(
    id = id,
    title = title,
    url = url,
    thumbnail = thumbnail,
    creator = creator,
    creator_url = null,
    license = license,
    license_version = null,
    license_url = null,
    provider = null,
    source = source,
    tags = null,
    attribution = null,
    height = height,
    width = width
)