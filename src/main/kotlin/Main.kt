package ru.melowetty

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.melowetty.model.KudagoResponse
import ru.melowetty.model.News

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            encodeDefaults = true
            classDiscriminator = "#class"
        })
    }
}

private val fields = listOf("id", "title", "publication_date", "place", "description", "site_url", "favorites_count", "comments_count")

fun main() {
    val news = runBlocking {
        getNews(page = 1)
    }

    println(news)
    println()

    val period = LocalDate.now().minusDays(30).rangeTo(LocalDate.now())

    println(news.getMostRatedNews(period = period))

    println("Hello World!")
}

suspend fun getNews(page: Int = 1, count: Int = 100): List<News> {
    val response: KudagoResponse = client.get("https://kudago.com/public-api/v1.4/news") {
        url {
            parameters.append("location", "spb")
            parameters.append("fields", fields.joinToString(","))
            parameters.append("expand", "place")
            parameters.append("page_size", count.toString())
            parameters.append("page", page.toString())
        }
    }.body()
    return response.results
}

fun List<News>.getMostRatedNews(period: ClosedRange<LocalDate>, count: Int = 20): List<News> {
    return filter { period.contains(it.publicationDate.toLocalDate()) }
        .sortedBy { it.rating }
        .drop(count)
}