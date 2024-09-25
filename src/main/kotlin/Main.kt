package ru.melowetty

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ru.melowetty.Extensions.Companion.getMostRatedNews
import ru.melowetty.dsl.html
import ru.melowetty.model.KudagoResponse
import ru.melowetty.model.News
import ru.melowetty.service.KudagoApiService
import ru.melowetty.service.NewsStorageService
import ru.melowetty.service.NewsViewService

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

private val kudagoApiService = KudagoApiService(client)
private val newsStorageService = NewsStorageService()
private val newsViewService = NewsViewService()

fun main() {
    val news = kudagoApiService.getNews()
    println(news.take(10).toList())
    println()

    val period = LocalDate.now().minusDays(30).rangeTo(LocalDate.now())
    println(news.getMostRatedNews(period = period, count = 5))

    newsStorageService.saveNews("news.csv", news.take(5).toList())
    println()

    newsViewService.getNewsAsHtml(news.first())
}


