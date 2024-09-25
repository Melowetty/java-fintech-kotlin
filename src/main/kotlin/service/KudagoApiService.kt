package ru.melowetty.service

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.runBlocking
import ru.melowetty.client
import ru.melowetty.model.KudagoResponse
import ru.melowetty.model.News

class KudagoApiService(
    private val httpClient: HttpClient
) {
    private val fields =
        listOf("id", "title", "publication_date", "place", "description", "site_url", "favorites_count", "comments_count")


    fun getNews(page: Int = 1, count: Int = 100): Sequence<News> {
        return sequence {
            var nextPage = page
            while (true) {
                val news = getNewsByPage(nextPage, count)
                if (news.isEmpty()) break
                yieldAll(news)
                nextPage += 1
            }
        }
    }

    private fun getNewsByPage(page: Int, count: Int): List<News> {
        val response = runBlocking<KudagoResponse> {
            client.get("https://kudago.com/public-api/v1.4/news") {
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
    }
}