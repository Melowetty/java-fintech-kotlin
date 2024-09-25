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
}