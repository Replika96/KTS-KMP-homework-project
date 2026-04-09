package org.kts.tazmin.feature.course_reviews.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.course_reviews.data.mapper.ReviewMapperImpl
import org.kts.tazmin.feature.course_reviews.data.network.ReviewApiImpl
import org.kts.tazmin.feature.course_reviews.data.repository.ReviewRepositoryImpl
import org.kts.tazmin.feature.course_reviews.domain.mapper.ReviewMapper
import org.kts.tazmin.feature.course_reviews.domain.network.ReviewApi
import org.kts.tazmin.feature.course_reviews.domain.repository.ReviewRepository
import org.kts.tazmin.feature.course_reviews.presentation.viewmodel.ReviewViewModel

val reviewModule = module {
    factory<ReviewApi> {
        ReviewApiImpl(get(named("authClient")))
    }
    factory<ReviewRepository> {
        ReviewRepositoryImpl(
            api = get(),
            mapper = get(),
            reviewDao = get(),
            queryDao = get(),
        )
    }
    // mapper
    factory<ReviewMapper> { ReviewMapperImpl() }

    // ViewModel
    viewModel { (courseId: Long) ->
        ReviewViewModel(
            courseId = courseId,
            reviewRepository = get()
        )
    }
}
