package ru.melowetty.model

import kotlinx.serialization.Serializable

@Serializable
data class KudagoResponse(
    val results: List<News>
)
