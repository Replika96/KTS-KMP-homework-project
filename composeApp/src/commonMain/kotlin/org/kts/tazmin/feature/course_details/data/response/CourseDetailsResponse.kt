package org.kts.tazmin.feature.course_details.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailsResponse(
    val courses: List<CourseDetailsDto>
)

@Serializable
data class CourseDetailsDto(
    val id: Int,

    // Основная информация
    val title: String,
    val summary: String? = null,
    val description: String? = null,
    val cover: String? = null,
    val workload: String? = null, // длительность

    // Авторы
    val authors: List<Int>? = null,

    // Структура курса
    @SerialName("sections")
    val sectionIds: List<Int>? = null,

    // Рейтинг (ID summary)
    @SerialName("review_summary")
    val reviewSummaryId: Int? = null,

    // Количество учеников
    @SerialName("learners_count")
    val learnersCount: Int? = null,

    // Цена
    @SerialName("display_price")
    val displayPrice: String? = null,

    @SerialName("is_paid")
    val isPaid: Boolean? = null,

    // Скидки
    @SerialName("default_promo_code_price")
    val discountPrice: String? = null,

    @SerialName("default_promo_code_expire_date")
    val discountDeadline: String? = null,

    // Проверенный ли курс
    @SerialName("is_verified")
    val isVerified: Boolean? = null,

    // Сертификат
    @SerialName("with_certificate")
    val certificateAvailable: Boolean? = null,

    @SerialName("certificate")
    val certificateDescription: String? = null,

    // Целевая аудитория
    @SerialName("target_audience")
    val targetAudience: String? = null,

    // Требования
    val requirements: String? = null,

    // Язык
    val language: String? = null,

    // Прогресс (ID)
    @SerialName("progress")
    val progressId: String?,

    // Действия (поделиться, избранное)
    val actions: CourseActionsDto? = null,
    // преподы
    @SerialName("instructors")
    val instructorIds: List<Int>? = null

)

@Serializable
data class CourseActionsDto(
    @SerialName("share_url")
    val shareUrl: String? = null,

    @SerialName("is_favorite")
    val isFavorite: Boolean? = null
)
