package com.android.photogallery.models

data class ImageTag(
    val name: String,
    val accuracy: Float?,
)

// Модель для изображения
data class ImageResult(
    val id: String,
    val title: String?,
    val url: String,
    val thumbnail: String?,
    val creator: String?,
    val creator_url: String?,
    val license: String,
    val license_version: String?,
    val license_url: String?,
    val provider: String?,
    val source: String?,
    val tags: List<ImageTag>?,
    val attribution: String?,
    val height: Int?,
    val width: Int?,
)

// Модель ответа API
data class OpenverseResponse(
    val result_count: Int,
    val page_count: Int,
    val page_size: Int,
    val page: Int,
    val results: List<ImageResult>,
    val warnings: List<ApiWarning>?,
)

data class ApiWarning(
    val code: String,
    val message: String,
)