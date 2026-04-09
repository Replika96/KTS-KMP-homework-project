package org.kts.tazmin.feature.course_details.domain.repository

import kotlinx.coroutines.flow.Flow
import org.kts.tazmin.feature.course_details.domain.entity.CourseCTA
import org.kts.tazmin.feature.course_details.domain.entity.CourseHeader
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo

interface CourseDetailsRepository {
    fun observeHeader(courseId: Int): Flow<CourseHeader?>

    fun observeInfo(courseId: Int): Flow<CourseInfo?>

    fun observeCTA(courseId: Int): Flow<CourseCTA?>

    suspend fun refresh(courseId: Int): Result<Unit>
}

