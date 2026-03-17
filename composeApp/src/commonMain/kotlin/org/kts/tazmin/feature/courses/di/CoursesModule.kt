package org.kts.tazmin.feature.courses.di

import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.kts.tazmin.feature.courses.data.mapper.CourseMapper
import org.kts.tazmin.feature.courses.data.network.api.CoursesApi
import org.kts.tazmin.feature.courses.data.repository.CoursesRepositoryImpl
import org.kts.tazmin.feature.courses.domain.repository.CoursesRepository
import org.kts.tazmin.feature.courses.domain.usacase.GetCoursesUseCase
import org.kts.tazmin.feature.courses.domain.usacase.SearchCoursesUseCase
import org.kts.tazmin.feature.courses.presentation.viewmodel.CoursesViewModel
import org.kts.tazmin.feature.profile.data.mapper.UserMapper

val coursesModule = module {
    // API
    single {
        CoursesApi(get(named("authClient")))
    }
    // Repository
    factory<CoursesRepository> {
        CoursesRepositoryImpl(
            api = get(),
            courseDao = get(),
            courseMapper = get()
        )
    }
    single<CourseMapper> { CourseMapper }
    // UseCases
    factory { GetCoursesUseCase(get()) }
    factory { SearchCoursesUseCase(get()) }
    // ViewModel
    viewModel {
        CoursesViewModel(
            getCoursesUseCase = get(),
            searchCoursesUseCase = get()
        )
    }
}
