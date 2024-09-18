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
import ru.melowetty.dsl.html
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

private val fields =
    listOf("id", "title", "publication_date", "place", "description", "site_url", "favorites_count", "comments_count")

fun main() {
    val news = runBlocking {
        getNews(page = 1)
    }

    println(news)
    println()

    val period = LocalDate.now().minusDays(30).rangeTo(LocalDate.now())
    println(news.getMostRatedNews(period = period))

    saveNews("news.csv", news)
    println()

    getNewsAsHtml(news.first())
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

fun saveNews(path: String, news: Collection<News>) {
    val filePath = Path.of(path)

    if (filePath.parent?.let { Files.exists(it) } == true)
        throw IllegalArgumentException("Такого пути нет!")

    if (Files.exists(filePath))
        throw IllegalArgumentException("Файл с таким именем уже существует!")

    filePath.toFile().printWriter().use { writer ->
        val delimiter = ";"
        val fieldsName = arrayOf(
            "id", "title", "publicationDate", "place", "description", "siteUrl",
            "favoritesCount", "commentsCount", "rating"
        )

        writer.println(fieldsName.joinToString(delimiter))

        news.map {
            arrayOf<String>(
                it.id.toString(), it.title, it.publicationDate.toString(), it.place?.id.toString(), it.description,
                it.siteUrl, it.favoritesCount.toString(), it.commentsCount.toString(), it.rating.toString()
            )
        }
            .map { it.joinToString(delimiter) }
            .forEach { writer.println(it) }
    }
}

fun getNewsAsHtml(news: News) {
    html {
        body {
            header(level = 1) {
                +news.title
            }

            header(level = 2) {
                +news.publicationDate.toString()
                +news.place?.toString()
            }

            text {
                +news.description
            }

            text {
                bold {
                    +news.favoritesCount.toString()
                    +news.commentsCount.toString()
                    +news.rating.toString()
                }
            }

            text {
                link(href = news.siteUrl) {
                    +"Ссылка на статью"
                }
            }
        }
    }
}
