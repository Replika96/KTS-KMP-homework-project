package org.kts.tazmin.feature.catalog.domain.repository

interface CoursesListRepository {
    suspend fun loadNextPage(courseListId: Int): Result<Boolean>

    suspend fun refresh(courseListId: Int): Result<Unit>
}

