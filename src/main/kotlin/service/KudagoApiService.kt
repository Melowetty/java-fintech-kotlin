package ru.melowetty.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import ru.melowetty.model.KudagoResponse
import ru.melowetty.model.News

class KudagoApiService(
    private val httpClient: HttpClient
) {
    private val logger = KotlinLogging.logger {  }

    private val fields =
        listOf("id", "title", "publication_date", "place", "description", "site_url", "favorites_count", "comments_count")


    fun getNews(page: Int = 1, count: Int = 100): Sequence<News> {
        return sequence {
            var nextPage = page
            while (true) {
                try {
                    val news = getNewsByPage(nextPage, count)
                    if (news.isEmpty()) break
                    yieldAll(news)
                    nextPage += 1
                } catch (runtimeException: RuntimeException) {
                    logger.error { runtimeException }
                    break
                }
            }
        }
    }

    private fun getNewsByPage(page: Int, count: Int): List<News> {
        try {
            val response = runBlocking<KudagoResponse> {
                httpClient.get("https://kudago.com/public-api/v1.4/news") {
                    url {
                        parameters.append("location", "spb")
                        parameters.append("fields", fields.joinToString(","))
                        parameters.append("expand", "place")
                        parameters.append("page_size", count.toString())
                        parameters.append("page", page.toString())
                    }
                }.body()
            }
            return response.results
        } catch (serverResponseException: ServerResponseException) {
            logger.error { serverResponseException }
            throw RuntimeException("Ошибка во время получения ответа для получения новостей")
        } catch (clientRequestException: ClientRequestException) {
            logger.error { clientRequestException }
            throw RuntimeException("Ошибка во время отправки запроса для получения новостей")
        } catch (ioException: IOException) {
            logger.error { ioException }
            throw RuntimeException("Проблема с интернетом")
        } catch (exception: Exception) {
            logger.error { exception }
            throw RuntimeException("Произошла ошибка во время получения новостей")
        }
    }
}