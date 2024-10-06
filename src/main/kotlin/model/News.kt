package ru.melowetty.model

import java.time.LocalDateTime
import kotlin.math.exp
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.melowetty.serializator.LocalDateTimeSerializer

@Serializable
data class News(
    val id: Int,
    val title: String,

    @Contextual
    @SerialName("publication_date")
    @Serializable(with = LocalDateTimeSerializer::class)
    val publicationDate: LocalDateTime,
    val place: Place?,
    val description: String,

    @SerialName("site_url")
    val siteUrl: String,

    @SerialName("favorites_count")
    val favoritesCount: Int,

    @SerialName("comments_count")
    val commentsCount: Int,
) {
    val rating: Double by lazy {
        1 / (1 + exp(-(favoritesCount.toDouble() / (commentsCount + 1))))
    }
}
