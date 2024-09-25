package ru.melowetty.service

import ru.melowetty.dsl.html
import ru.melowetty.model.News

class NewsViewService {
    fun getNewsAsHtml(news: News) {
        html {
            body {
                header(level = 1) {
                    +news.title
                }

                header(level = 2) {
                    +"Дата публикации: ${news.publicationDate.toString()}"
                    +"Место: ${news.place?.toString()}"
                }

                text {
                    +news.description
                }

                text {
                    bold {
                        +"Лайков: ${news.favoritesCount.toString()}"
                        +"Количество комментариев: ${news.commentsCount.toString()}"
                        +"Рейтинг: ${news.rating.toString()}"
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
}