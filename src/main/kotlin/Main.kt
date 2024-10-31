package ru.melowetty

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import ru.melowetty.model.News
import ru.melowetty.service.KudagoApiService
import ru.melowetty.service.NewsStorageService
import ru.melowetty.service.NewsViewService

private val client = HttpClient(CIO) {
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
        val n = 5
        val workers = newFixedThreadPoolContext(n, "News workers")
        val channel = Channel<List<News>>()
        val works = mutableListOf<Job>()
        val endPage = 30
        val startTime = System.currentTimeMillis()
        for (i in 1 .. n) {
            works.add(CoroutineScope(workers).launch {
                for(page in i..endPage step n) {
                    logger.info { "Новости запрошены, страница $page" }
                    val news = kudagoApiService.getNews(page)
                    channel.send(news)
                }
            })
    }

        CoroutineScope(Dispatchers.IO).launch {
            works.joinAll()
            val endTime = System.currentTimeMillis()
            logger.info { "Канал закрылся через ${endTime - startTime} мс" }
            channel.close()
        }

        newsStorageService.saveNews("news.csv", channel)

    } catch (e: Exception) {
        logger.error { e }
    }
}


