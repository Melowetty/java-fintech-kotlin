package ru.melowetty

import java.time.LocalDate
import ru.melowetty.model.News

class Extensions {
    companion object {
        fun Sequence<News>.getMostRatedNews(period: ClosedRange<LocalDate>, count: Int = 20): List<News> {
            return filter { period.contains(it.publicationDate.toLocalDate()) }
                .take(count)
                .sortedBy { it.rating }
                .toList()
        }
    }
}