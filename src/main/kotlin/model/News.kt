package ru.melowetty.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class News(
    val id: Int,
    val title: String,
    val place: Place?,
    val description: String,

    @SerialName("site_url")
    val siteUrl: String,

    @SerialName("favorites_count")
    val favoritesCount: Int,

    @SerialName("comments_count")
    val commentsCount: Int,
)
