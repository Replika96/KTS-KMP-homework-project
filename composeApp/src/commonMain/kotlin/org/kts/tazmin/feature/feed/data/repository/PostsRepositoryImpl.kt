package org.kts.tazmin.feature.feed.data.repository

import org.kts.tazmin.feature.feed.data.model.FeedResult
import org.kts.tazmin.feature.feed.domain.entity.Post
import org.kts.tazmin.feature.feed.domain.repository.PostsRepository
import org.kts.tazmin.feature.feed.presentation.state.FeedType

class PostsRepositoryImpl : PostsRepository {
    //мок
    val mockPosts = listOf(
        Post(
            id = "1",
            title = "Первый пост 🎉",
            author = "admin",
            subredcat = "cat",
            content = "Это наш первый пост в Reddit клиенте. Работает на CMP и будет доступен на Android и iOS(возможно)!",
            upvotes = 128,
            commentCount = 15,
            timeAgo = "2ч"
        ),
        Post(
            id = "2",
            title = "Кот на месте 🐱",
            author = "Кот Альберт",
            subredcat = "cat",
            content = "Мяу! Я пришел посмотреть что тут пишут. Всех обнял лапками.",
            upvotes = 356,
            commentCount = 42,
            imageUrl = "https://i.postimg.cc/1XV1P57q/anon.jpg",
            timeAgo = "5ч"
        ),
        Post(
            id = "3",
            title = "Как ваши дела?",
            author = "Аноним",
            subredcat = "cat",
            content = "Ребята, как настроение? Что читаете? Делитесь в комментариях!",
            upvotes = 89,
            commentCount = 23,
            imageUrl = "https://i.postimg.cc/cC7y65Zx/bingchilin.jpg",
            timeAgo = "12ч"
        ),
        Post(
            id = "4",
            title = "Kotlin Multiplatform это мощно!😮",
            author = "dev_guru",
            subredcat = "cat",
            content = "Пишем код один раз, а работает везде. CMP позволяет делать UI на всех платформах. Красота!",
            upvotes = 567,
            commentCount = 78,
            imageUrl = null,
            timeAgo = "1д"
        ),
        Post(
            id = "5",
            title = "Jetpack Compose vs SwiftUI",
            author = "mobile_dev",
            subredcat = "cat",
            content = "Интересно сравнить эти два фреймворка. CMP позволяет использовать Compose на iOS, но насколько это хорошо?",
            upvotes = 234,
            commentCount = 56,
            imageUrl = null,
            timeAgo = "2д"
        )
    )

    override suspend fun getFeed(
        type: FeedType,
        after: String?,
        limit: Int
    ): Result<FeedResult> {
        return try{
            val startIndex = after?.toIntOrNull() ?: 0
            val endIndex = (startIndex + limit).coerceAtMost(mockPosts.size)

            val subList = mockPosts.subList(startIndex, endIndex)

            val nextAfter =
                if (endIndex < mockPosts.size) {
                    endIndex.toString()
                } else {
                    null
                }

            Result.success(
                FeedResult(
                    posts = subList,
                    after = nextAfter
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
