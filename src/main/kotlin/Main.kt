package ru.melowetty

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.request
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import io.ktor.serialization.kotlinx.json.json
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

private val fields = listOf("id", "title", "place", "description", "site_url", "favorites_count", "comments_count")

fun main() {
    val news = runBlocking {
        getNews(page = 2)
    }

    println(news)

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