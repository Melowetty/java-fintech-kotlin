package ru.melowetty

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.time.LocalDate
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import ru.melowetty.Extensions.Companion.getMostRatedNews
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

private val logger = KotlinLogging.logger {  }

private val kudagoApiService = KudagoApiService(client)
private val newsStorageService = NewsStorageService()
private val newsViewService = NewsViewService()

fun main() {
    try {
        val news = kudagoApiService.getNews()
        logger.info { news.take(10).toList() }

        val period = LocalDate.now().minusDays(30).rangeTo(LocalDate.now())
        logger.info { news.getMostRatedNews(period = period, count = 5) }

        newsStorageService.saveNews("news.csv", news.take(5).toList())

        newsViewService.getNewsAsHtml(news.first())
    } catch (e: Exception) {
        logger.error { e }
    }
}


