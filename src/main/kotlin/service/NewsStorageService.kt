package ru.melowetty.service

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import mu.KotlinLogging
import ru.melowetty.model.News

class NewsStorageService {
    private val logger = KotlinLogging.logger {  }

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

            try {
                writer.println(fieldsName.joinToString(delimiter))

                news.map {
                    arrayOf<String>(
                        it.id.toString(), it.title, it.publicationDate.toString(), it.place?.id.toString(), it.description,
                        it.siteUrl, it.favoritesCount.toString(), it.commentsCount.toString(), it.rating.toString()
                    )
                }
                    .map { it.joinToString(delimiter) }
                    .forEach { writer.println(it) }
            } catch (ioException: IOException) {
                logger.error { ioException }
            }
        }
    }
}