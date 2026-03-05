package org.kts.tazmin.feature.courses.data.repository

import org.kts.tazmin.feature.courses.data.model.CoursesResponse
import org.kts.tazmin.feature.courses.data.model.Meta
import org.kts.tazmin.feature.courses.domain.entity.Course
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository

class CoursesRepositoryImpl : CoursesRepository {
    //мок данные
    val mockCourses = listOf(
        Course(
            id = 1,
            title = "Android Development with Kotlin",
            description = "Полный курс по разработке Android приложений. От основ до создания профессиональных приложений.",
            author = "JetBrains Academy",
            coverUrl = "https://i.postimg.cc/cC7y65Zx/bingchilin.jpg",
            rating = 4.8,
            studentsCount = 12000
        ),
        Course(
            id = 2,
            title = "Kotlin Multiplatform",
            description = "Изучаем KMP и создаем кроссплатформенные приложения для Android и iOS.",
            author = "JetBrains",
            coverUrl = "https://i.postimg.cc/1XV1P57q/anon.jpg",
            rating = 4.7,
            studentsCount = 8500
        ),
        Course(
            id = 3,
            title = "SwiftUI для начинающих",
            description = "Создавайте красивые интерфейсы для iOS приложений с помощью SwiftUI",
            author = "Apple Academy",
            coverUrl = "https://i.postimg.cc/cC7y65Zx/bingchilin.jpg",
            rating = 4.9,
            studentsCount = 7500
        ),
        Course(
            id = 4,
            title = "Python для Data Science",
            description = "Изучите Python, pandas, numpy и создавайте модели машинного обучения",
            author = "Data Science Institute",
            coverUrl = "https://i.postimg.cc/1XV1P57q/anon.jpg",
            rating = 4.6,
            studentsCount = 15000
        )
    )

    override suspend fun getCourses(page: Int, limit: Int): Result<CoursesResponse> {
        return try {
            val startIndex = (page - 1) * limit
            val endIndex = (startIndex + limit).coerceAtMost(mockCourses.size)

            val subList = if (startIndex < mockCourses.size) {
                mockCourses.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

            val hasNext = endIndex < mockCourses.size

            Result.success(
                CoursesResponse(
                    courses = subList,
                    meta = Meta(
                        page = page,
                        hasNext = hasNext,
                        hasPrevious = page > 1
                    )
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
