package org.kts.tazmin.feature.course_details.data.mapper

import org.kts.tazmin.feature.course_details.data.local.CourseDetailsEntity
import org.kts.tazmin.feature.course_details.domain.entity.CourseInfo
import org.kts.tazmin.feature.profile.data.local.UserEntity
import org.kts.tazmin.feature.profile.data.mapper.UserDbMapper

class CourseInfoMapper(
    private val userDbMapper: UserDbMapper
) {
    fun mapToDomain(
        course: CourseDetailsEntity,
        authors: List<UserEntity>,
        instructors: List<UserEntity>
    ) = CourseInfo(
        description = course.description ?: "",
        summary = course.summary,
        requirements = course.requirements?.split("\n") ?: emptyList(),
        targetAudience = course.targetAudience?.split("\n") ?: emptyList(),
        duration = course.workload,
        language = course.language,
        certificateAvailable = course.certificateAvailable,
        certificateDescription = course.certificateDescription,
        learnersCount = course.learnersCount,
        authors = authors.map { userDbMapper.toDomain(it) },
        instructors = instructors.map { userDbMapper.toDomain(it) },
        coverUrl = course.coverUrl,

    )
}

